package com.example.kaamwaale

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.kaamwaale.daos.AllDaosObjectCreate
import com.example.kaamwaale.databinding.ActivitySplashScreenBinding
import com.example.kaamwaale.databinding.ActivitySplashScreenTwoBinding

class SplashScreenTwo : AppCompatActivity() {
    lateinit var binding: ActivitySplashScreenTwoBinding
    var player:MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen_two)
        binding= ActivitySplashScreenTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        musicStart()

//        supportActionBar!!.hide()
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

        Handler().postDelayed({
            startActivity(Intent(applicationContext, Login::class.java))
            finish()
        }, 3950)
    }
    fun musicStart(){
        player=MediaPlayer.create(this, R.raw.music)
        player?.start()
        player?.setOnCompletionListener { MediaPlayer.OnCompletionListener {
            player?.release()
            player=null
        } }
    }
}