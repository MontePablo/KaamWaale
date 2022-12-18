package com.example.kaamwaale

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.kaamwaale.daos.FirebaseDao.TAG
import com.example.kaamwaale.daos.FirebaseDao.auth
import com.example.kaamwaale.daos.UserDao
import com.example.kaamwaale.databinding.ActivityLoginBinding
import com.example.kaamwaale.databinding.ActivityProfileBusinessBinding
import com.example.kaamwaale.databinding.ActivityProfileGeneralInfoBinding
import com.example.kaamwaale.databinding.CustomViewSkillBinding
import com.example.kaamwaale.models.User

class ProfileBusiness : AppCompatActivity() {
    lateinit var binding: ActivityProfileBusinessBinding
    lateinit var user:User
    var skillsArray=ArrayList<CustomViewSkillBinding>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileBusinessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadUserIntoView(auth.uid!!)

        binding.skillAdd.setOnClickListener(View.OnClickListener { addSkillView() })
        addSkillView()
        binding.save.setOnClickListener(View.OnClickListener { upload() })
        binding.cancel.setOnClickListener(View.OnClickListener { onBackPressed() })
    }

    private fun loadUserIntoView(uid: String) {
        UserDao.getUser(uid).addOnSuccessListener { document->
            document.toObject(User::class.java)?.let { user ->
                this.user=user
                binding.profession.setText(user.profession)
                binding.pan.setText(user.pan)
                binding.gstin.setText(user.gstin)
            }
        }
    }
    fun addSkillView(){
        var viewBinding=CustomViewSkillBinding.inflate(layoutInflater)
        viewBinding.root.id=View.generateViewId()
        binding.skillsGroup.addView(viewBinding.root)
        binding.skillsFlow.addView(viewBinding.root)
        skillsArray.add(viewBinding)

    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun upload(){
        val profession=binding.profession.text.toString()
        val skills=getSkillAsString()
        val pan=binding.pan.text.toString()
        val gstin=binding.gstin.text.toString()
        var count=0
        if(!profession.isNullOrBlank()){
            user.profession=profession
            count++
        }
        if(!pan.isNullOrBlank()){
            user.pan=pan
            count++
        }
        if(!gstin.isNullOrBlank()){
            user.gstin=gstin
            count++
        }
        if(skills.isNotEmpty()) {
            user.skills = skills
            count++
        }
        if(count>=3){
            user.userType="seller"
        }
        UserDao.addUser(user).addOnSuccessListener {
            Log.d(TAG,"user upload sucess")
            Toast.makeText(this,"sucess",Toast.LENGTH_SHORT).show()
            if(intent.getBooleanExtra("becomeSeller",false)){
                finish()
                startActivity(Intent(this,CreateGigs::class.java))
            }else{
                finish()
                startActivity(Intent(this,MainActivity::class.java))
            }
        }.addOnFailureListener {
            Log.d(TAG,"user upload failed:${it.localizedMessage}")
        }

    }
    fun getSkillAsString(): ArrayList<String> {
        var arr=ArrayList<String>()
        for(viewBinding in skillsArray){
            val ss=viewBinding.customViewSkill.text.toString()
            if(!ss.isNullOrBlank()){
                arr.add(ss)
            }
        }
        return arr
    }
    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
    }
}