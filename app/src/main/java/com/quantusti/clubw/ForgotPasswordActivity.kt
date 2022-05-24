package com.quantusti.clubw

import ViewModels.ForgotPasswordViewModels
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.quantusti.clubw.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var binding = DataBindingUtil.setContentView<ActivityForgotPasswordBinding>(this, R.layout.activity_forgot_password)
        binding.forgotpasswordModel = ForgotPasswordViewModels(this, binding)
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = this.getColor(R.color.colorBackgroud);
    }
}