package com.v2ray.ang.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.v2ray.ang.R

class SettingsNewActivity : BaseActivity() {
    data class SettingItem(
        @DrawableRes val icon: Int,
        val title: String,
        val subtitle: String? = null
    )

    private val requestSubSettingActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    }

    //@SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_new)

        val items = listOf(
            SettingItem(R.drawable.ic_router, getString(R.string.menu_route)),
            SettingItem(R.drawable.ic_dns, "DNS"),
            SettingItem(R.drawable.ic_location,  getString(R.string.menu_loc)),
            SettingItem(R.drawable.ic_info,  getString(R.string.menu_about), subtitle = "1.0.12"),
            SettingItem(R.drawable.ic_share,  getString(R.string.menu_share))
        )

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        // 关键：把这个 Toolbar 设置为 ActionBar
        setSupportActionBar(toolbar)

        // 显示返回箭头（导航按钮）
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_settings)

        val rv = findViewById<RecyclerView>(R.id.rvSettings)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = SettingsAdapter(items) { item ->
            when (item.title) {
                getString(R.string.menu_route) -> requestSubSettingActivity.launch(Intent(this, RoutingSettingActivity::class.java))
                "DNS" ->  requestSubSettingActivity.launch(Intent(this, DnsSettingsActivity::class.java))
//                 getString(R.string.menu_loc) -> startActivity(Intent(this, LocationSettingsActivity::class.java))
                getString(R.string.menu_about) -> requestSubSettingActivity.launch(Intent(this, AboutActivity::class.java))
//                "分享链接" -> startActivity(Intent(this, ShareActivity::class.java))
                else -> { /* do nothing */ }
            }
        }
    }
}
