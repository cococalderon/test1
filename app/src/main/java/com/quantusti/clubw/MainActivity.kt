package com.quantusti.clubw

import ViewModels.MainViewModels
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.quantusti.clubw.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var main: MainViewModels? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val _binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        main = MainViewModels(this, _binding)
        _binding.mainModel = main
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = this.getColor(R.color.colorBackgroud);
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        main!!.onActivityResult(requestCode, resultCode, data)
    }
}