package com.v2ray.ang.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.v2ray.ang.R
import com.v2ray.ang.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        binding.btnLogin.setOnClickListener {
            val account = binding.etEmail.text.toString().trim()
            val pwd     = binding.etPwd.text.toString().trim()
            if (account.isEmpty() || pwd.isEmpty()) {
                Toast.makeText(context, "请输入账号和密码", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "登录按钮被点啦", Toast.LENGTH_SHORT).show()
            }
        }
    }
}