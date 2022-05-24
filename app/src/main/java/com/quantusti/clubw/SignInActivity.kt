package com.quantusti.clubw

import Library.MemoryData
import Library.Ref
import ViewModels.SignInViewModels
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.quantusti.clubw.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private var memoryData: MemoryData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Ref.setContext(this)

        //check if client is already log in
        memoryData = MemoryData.getInstance(this)
        if (memoryData!!.getData("GLOBAL_CLIENT_ID") == "") {

            var binding = DataBindingUtil.setContentView<ActivitySignInBinding>(
                this,
                R.layout.activity_sign_in
            )
            binding.signinModel = SignInViewModels(this, binding)
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = this.getColor(R.color.colorBackgroud);
        } else {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}