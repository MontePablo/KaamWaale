package com.example.kaamwaale

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.example.kaamwaale.daos.FirebaseDao.TAG
import com.example.kaamwaale.daos.UserDao
import com.example.kaamwaale.databinding.ActivityBecomeSellerBinding
@RequiresApi(Build.VERSION_CODES.R)
class BecomeSeller : AppCompatActivity() {
    lateinit var binding:ActivityBecomeSellerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBecomeSellerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button.setOnClickListener(View.OnClickListener { func() })
        Log.d(TAG,"BecomeSellar on")
    }
    fun func(){
        if(UserDao.user.userType!="seller")
            startActivity(Intent(this,ProfileBusiness::class.java).putExtra("becomeSeller",true))
        else{
            startActivity(Intent(this,CreateGigs::class.java))
        }
        finish()
    }
}