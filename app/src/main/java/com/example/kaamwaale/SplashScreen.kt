package com.example.kaamwaale

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.kaamwaale.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var animation: Animation =AnimationUtils.loadAnimation(applicationContext,R.anim.splash_animation)
        binding.splashLogo.startAnimation(animation)

//        supportActionBar!!.hide()
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

        Handler().postDelayed({
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }, 3000)
    }
}