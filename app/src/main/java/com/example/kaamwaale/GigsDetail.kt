package com.example.kaamwaale

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.kaamwaale.adapters.SliderAdapter
import com.example.kaamwaale.daos.FirebaseDao.TAG
import com.example.kaamwaale.daos.UserDao
import com.example.kaamwaale.databinding.*
import com.example.kaamwaale.models.Gig
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import org.json.JSONObject


class GigsDetail : AppCompatActivity(), PaymentResultListener{
    lateinit var binding:ActivityGigsDetailBinding
     var gig: Gig? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityGigsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        gig = Gson().fromJson(intent.getStringExtra("gig"), Gig::class.java)
        loadData()
        binding.gigChat.setOnClickListener(View.OnClickListener { chat()})
        Checkout.preload(applicationContext); //for loading payment page quickly later
//        binding.gigBuy.setOnClickListener(View.OnClickListener { newOrderDialog() })
        binding.gigBuy.setOnClickListener(View.OnClickListener {
            OrderDialog.processOrder(this,this,gig,layoutInflater)
        })

    }

    private fun chat() {
        var intent=Intent(this,Chat::class.java)
            .putExtra("gigName",gig!!.title)
            .putExtra("receiverId",gig!!.uid)
            .putExtra("senderImage",UserDao.user.pic)
            .putExtra("receiverImage",gig!!.userImage)
        startActivity(intent)
    }

    fun loadData() {
        binding.sliderView.setSliderAdapter(SliderAdapter(gig!!.images))
        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION)
        binding.sliderView.startAutoCycle()

        val sellerBinding=ItemUsersBinding.inflate(layoutInflater)
        sellerBinding.itemUserName.text=gig!!.name
        sellerBinding.itemUserProfession.text=gig!!.profession
        sellerBinding.itemUserSkills.text=getSkill(gig!!.skills)
        sellerBinding.itemUserRating.rating=gig!!.userAvgRating.toFloat()
        Glide.with(this).load(gig!!.userImage).into(sellerBinding.itemUserImage)
        binding.sellerLayout.addView(sellerBinding.root)


        binding.headline.text=gig!!.title
        binding.description.text=gig!!.description
        addReviews()
        addPackages()

    }
    fun getSkill(arr:ArrayList<String>):String{
        var skills=StringBuffer()
        for(s in arr){
            skills.append("$s ")
        }
        return skills.toString()
    }
    fun addReviews(){
        for(rating in gig!!.ratings){
            val name=rating["name"] as String
            val star=rating["rating"] as String
            val comment=rating["comment"] as String
            val image=rating["image"] as String
            val date=rating["date"] as String

            val viewBinding=CustomViewRatingsBinding.inflate(layoutInflater)
            viewBinding.name.text=name
            Glide.with(this).load(image).into(viewBinding.image)
            viewBinding.comment.text=comment
            viewBinding.date.text=date
            viewBinding.ratingBar.rating=star.toFloat()
            binding.ratingsLayout.addView(viewBinding.root)
        }
    }
    fun addPackages(){
        for(pack in gig!!.packages){
            var viewBinding=CustomViewPackageViewBinding.inflate(layoutInflater)
            viewBinding.title.text=pack.title
            viewBinding.price.text=pack.price
            viewBinding.description.text=pack.description
            viewBinding.unit.text=pack.unit
            binding.packagesLayout.addView(viewBinding.root)
        }
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
    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
    }

}
