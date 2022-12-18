package com.example.kaamwaale

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kaamwaale.daos.FirebaseDao.TAG
import com.example.kaamwaale.daos.FirebaseDao.auth
import com.example.kaamwaale.daos.StorageDao
import com.example.kaamwaale.daos.UserDao
import com.example.kaamwaale.databinding.ActivityProfileGeneralInfoBinding
import com.example.kaamwaale.models.User
import com.yalantis.ucrop.UCrop
import java.io.File
import java.lang.StringBuilder
import java.util.*

class ProfileGeneral : AppCompatActivity() {
    lateinit var binding: ActivityProfileGeneralInfoBinding
    lateinit var user:User
    var imageLink=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileGeneralInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadUserIntoView(auth.uid!!)
        binding.save.setOnClickListener(View.OnClickListener { upload() })
        binding.changeImageButton.setOnClickListener(View.OnClickListener { photoPic()})

    }
    private fun imageUpload(uri: Uri) {
        val fileName=uri.hashCode().toString()
        StorageDao.uploadProfileImage(uri,fileName)?.addOnSuccessListener {
            Log.d(TAG,"upload success")
            StorageDao.getImageUrlOfGigs(fileName)!!.addOnSuccessListener {
                imageLink=it.toString()
                Glide.with(this).load(it).into(binding.image)
                Log.d(TAG,"getting Url success ${imageLink}")
            }
        }?.addOnFailureListener { Log.d(TAG,"uploadImage onFailure: ${it.localizedMessage}") }
    }

    fun photoPic(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 123)
    }
    fun startCropImage(sourceUri: Uri) {
        val maxWidth=500
        val maxHeight=500
//        val destinationUri=file1.toUri()
        var dest_uri=StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString()
        var destinationUri=Uri.fromFile(File(cacheDir,dest_uri))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(9F, 9F)
            .withMaxResultSize(maxWidth, maxHeight)
            .start(this);
        Log.d(TAG,"fuckit")
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK && requestCode==123){
            Log.d(TAG,"gotIt")
            startCropImage(data!!.data!!)
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri: Uri = UCrop.getOutput(data!!)!!
            val fileName=resultUri.hashCode().toString()
            StorageDao.uploadProfileImage(resultUri, fileName)!!.addOnSuccessListener {
                Log.d(TAG,"upload success")
                getAndUploadUrlOfImage(fileName)
            }.addOnFailureListener {
                Log.d(TAG,"uploadImage onFailure: ${it.localizedMessage}")
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError: Throwable = UCrop.getError(data!!)!!
        }
    }
    fun getAndUploadUrlOfImage(fileName:String){
        StorageDao.getImageUrlOfProfile(fileName)!!.addOnSuccessListener {
            val imageLink=it.toString()
            Glide.with(this).load(imageLink).into(binding.image)
            UserDao.user.pic=imageLink
            UserDao.addUser(UserDao.user)
            Log.d(TAG,"getting Url success ${imageLink}")
        }
    }
     fun loadUserIntoView(uid: String) {
        UserDao.getUser(uid).addOnSuccessListener { document->
            document.toObject(User::class.java)?.let { user ->
                this.user=user
                Glide.with(binding.image.context).load(user.pic).into(binding.image)
                binding.name.setText(user.name)
                binding.mobile.setText(user.mobile)
                binding.email.setText(user.email)
                binding.dob.setText(user.dob)
                if(user.gender=="male") {
                    binding.male.isChecked=true
                } else{binding.female.isChecked=true}
            }
        }
    }
    private fun upload(){
        var email=binding.email.text.toString()
        var dob=binding.dob.text.toString()
        var mobile=binding.mobile.text.toString()
        var name=binding.name.text.toString()
        var gender=getGender()
        if(!email.isNullOrBlank()) {
            user.email = email
        }
        if(!dob.isNullOrBlank()) {
            user.dob = dob
        }
        if(!mobile.isNullOrBlank()) {
            user.mobile = mobile
        }
        if(!name.isNullOrBlank()) {
            user.name = name
        }
        if (!gender.isNullOrBlank()) {
            user.gender = gender
        }
        UserDao.addUser(user).addOnSuccessListener {
            Toast.makeText(this,"sucess",Toast.LENGTH_SHORT).show()
            Log.d(TAG,"user upload success")
            finish()
            startActivity(Intent(this,MainActivity::class.java))
        }.addOnFailureListener {
            Log.d(TAG,"user upload failed: ${it.localizedMessage}")
        }
    }
    fun getGender(): String {
        if(binding.radioGroupGender.checkedRadioButtonId==R.id.male){
            return "male"
        }
        return "female"
    }
    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
    }
}