package com.v2ray.ang.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.v2ray.ang.R
import com.v2ray.ang.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    private lateinit var btnLogin: TextView
    private lateinit var btnReg: TextView
    private lateinit var tabBarBg: ImageView
    private lateinit var indicator: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter =  LoginRegAdapter(this)

        btnLogin = findViewById(R.id.btnLogin)
        btnReg   = findViewById(R.id.btnReg)
        tabBarBg  = findViewById(R.id.tabBarBg)
        indicator = findViewById(R.id.indicator)

        // 3. 点击文字切换页面
        btnLogin.setOnClickListener {
            viewPager.currentItem = 0
        }
        btnReg.setOnClickListener {
            viewPager.currentItem = 1
        }

        // 4. 滑动时改文字颜色
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(pos: Int) = updateTabIndicator(pos)
        })

        // 默认页
        updateTabIndicator(0)
    }

    private fun updateTabIndicator(pos: Int) {
        val red = ContextCompat.getColor(this, R.color.blue_200)
        val gray = ContextCompat.getColor(this, R.color.black)

        btnLogin.setTextColor(if (pos == 0) red else gray)
        btnReg.setTextColor  (if (pos == 1) red else gray)
        // 如果想换背景，在这里 setBackgroundResource 即可

        tabBarBg.isSelected = (pos == 0)   // 0 左白，1 右蓝

        val target = if (pos == 0) btnLogin else btnReg
        // 等文字布局完
        target.post {
            // 1. 宽度 = 文字本身宽（去掉左右 padding）
            val txtPaint = target.paint
            val textWidth = txtPaint.measureText(target.text.toString())
            indicator.layoutParams.width = textWidth.toInt() - 140 // 多 4dp 视觉缓冲
            indicator.requestLayout()

            // 2. 水平居中：文字中心 - 横杠中心
            val targetCenterX = target.left + target.width / 2f
            val indicatorCenterX = indicator.width / 2f
            indicator.animate()
                .translationX(targetCenterX - indicatorCenterX)
                .setDuration(250)
                .start()
        }
    }

    class LoginRegAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment =
            if (position == 0) LoginFragment() else RegisterFragment()
    }
}