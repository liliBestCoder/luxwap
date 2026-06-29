package com.v2ray.ang.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.v2ray.ang.R
import com.v2ray.ang.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)

        binding.btnVerify.setOnClickListener {
            val phone = binding.etEmail.text.toString().trim()
            val code  = binding.etEmail.text.toString().trim()
            if (phone.isEmpty() || code.isEmpty()) {
                Toast.makeText(context, "请输入手机号和验证码", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "注册按钮被点啦", Toast.LENGTH_SHORT).show()
            }
        }
    }
}