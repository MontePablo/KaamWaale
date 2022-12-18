package com.example.kaamwaale

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kaamwaale.daos.AllDaosObjectCreate
import com.example.kaamwaale.daos.FirebaseDao
import com.example.kaamwaale.daos.FirebaseDao.TAG
import com.example.kaamwaale.daos.FirebaseDao.auth
import com.example.kaamwaale.daos.UserDao
import com.example.kaamwaale.databinding.ActivityMainBinding
import com.example.kaamwaale.databinding.NewPasswordBinding
import com.razorpay.PaymentResultListener


class MainActivity : AppCompatActivity(), PaymentResultListener {
    lateinit var binding: ActivityMainBinding
    lateinit var fragHomeInst:Home
    lateinit var fragSearchInst:Search
    lateinit var fragSaveInst:Save
    var currentFragmentId=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AllDaosObjectCreate
        replaceFragment(Home())
        binding.toolbar.setTitle("")
//        binding.logoImageLong.visibility= View.INVISIBLE
        setSupportActionBar(binding.toolbar)
        binding.bottomNav.setOnItemSelectedListener {
            currentFragmentId=it.itemId
            when(it.itemId){
                R.id.nav_home ->{
                    supportActionBar?.show()
                    fragHomeInst=Home()
                    replaceFragment(fragHomeInst)
                    }
                R.id.nav_search -> {
                    supportActionBar?.hide()
                    fragSearchInst=Search(this)
                    replaceFragment(fragSearchInst)
                }
                R.id.nav_saved ->{
                    supportActionBar?.show()
                    fragSaveInst=Save()
                    replaceFragment(fragSaveInst)
                }
            }
            true
        }

    }

    override fun onBackPressed() {
//        super.onBackPressed()
        when(currentFragmentId){
            R.id.nav_home ->{

            }
            R.id.nav_search -> {
                fragSearchInst.onBackPressed()
            }
        }

    }

    fun replaceFragment(fragment:Fragment){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container,fragment).commit()
    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(UserDao.user.userType=="seller"){
            menuInflater.inflate(R.menu.top_menu_seller,menu)
        }else {
            menuInflater.inflate(R.menu.top_menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
              R.id.nav_become_a_seller -> startActivity(Intent(this, BecomeSeller::class.java))
              R.id.nav_chat -> startActivity(Intent(this, Demo::class.java))
              R.id.nav_log_out -> {
                  FirebaseDao.auth.signOut()
                  startActivity(Intent(this, Login::class.java))
              }
            R.id.nav_my_gigs ->{
                startActivity(Intent(this,MyGigs::class.java))
            }
            R.id.nav_change_password -> {
                changePasswordDialog()
            }
            R.id.nav_personal_information -> {
                startActivity(Intent(this, ProfileGeneral::class.java))
            }
            R.id.nav_business_information -> {
                startActivity(Intent(this, ProfileBusiness::class.java))
            }
//            R.id.nav_settings -> Toast.makeText(applicationContext,"settings",Toast.LENGTH_SHORT).show()
            R.id.nav_orders -> startActivity(Intent(this,Orders::class.java))
//              else -> Toast.makeText(applicationContext,"${item.title}",Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changePasswordDialog() {
        var dialogBuilder= AlertDialog.Builder(this)
        var dialogBinding= NewPasswordBinding.inflate(layoutInflater)
        dialogBuilder.setView(dialogBinding.root)
        val dialog=dialogBuilder.create()
        dialog.show()

        dialogBinding.savePassword.setOnClickListener(View.OnClickListener {
            var newPswrd=dialogBinding.newPassword.text.toString()
            auth.currentUser!!.updatePassword(newPswrd)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User password updated.")
                        Toast.makeText(this,"User password updated",Toast.LENGTH_SHORT)
                        dialog.dismiss()
                    }
                }.addOnFailureListener { Log.d(TAG,"updatePswrd Failed: ${it.localizedMessage}") }
        })
        dialogBinding.cancelPassword.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
    }
    override fun onPaymentSuccess(p0: String?) {
        Log.d(TAG,"payment success $p0")
        Toast.makeText(this,"payment done!",Toast.LENGTH_SHORT).show()
        OrderDialog.passOrder()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Log.d(TAG,"payment failed $p0")
        Toast.makeText(this,"payment failed!",Toast.LENGTH_SHORT).show()
    }

}



