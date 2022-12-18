package com.example.kaamwaale

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.kaamwaale.daos.FirebaseDao
import com.example.kaamwaale.daos.FirebaseDao.TAG
import com.example.kaamwaale.daos.OrderDao
import com.example.kaamwaale.daos.StorageDao
import com.example.kaamwaale.databinding.ActivityOrderDetailBinding
import com.example.kaamwaale.databinding.CustomViewImageBinding
import com.example.kaamwaale.models.Gig
import com.example.kaamwaale.models.Order
import com.google.gson.Gson
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask

class OrderDetail : AppCompatActivity() {
    var imageViewsTable:Hashtable<Int,CustomViewImageBinding> = Hashtable<Int,CustomViewImageBinding>()
    var imagesList=ArrayList<String>()
    lateinit var binding:ActivityOrderDetailBinding
    var order:Order?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        order = Gson().fromJson(intent.getStringExtra("order"), Order::class.java)
        loadData()
    }

    private fun loadData() {
        binding.run {
            orderDetailDateTime.text = order!!.dateTime
            Glide.with(applicationContext).load(order!!.image).into(binding.orderDetailImage)
            orderDetailPrice.text = order!!.packPrice
            orderDetailQuantity.text = order!!.quantity
            orderDetailTitle.text = order!!.title
            orderDetailPackageName.text = order!!.packName

            if (order!!.sellerId == FirebaseDao.auth.uid.toString()) {
                if(order!!.clientConfirm && order!!.sellerConfirm){
                    orderDetailPaymentStatus.text = "Payment Received into your account!"
                    cnfirmlyout.visibility=View.GONE
                    order!!.status="Payment Received into your account!"
                    OrderDao.updateOrder(order!!)
                }
                addImageView()
                binding.sellerDlvryInstrctnLayout.visibility = View.VISIBLE
                confirmDlvryBtn.setOnClickListener(View.OnClickListener {
                    if(order!!.clientConfirm)
                        orderDetailPaymentStatus.text = "Payment Received into your account!"
                    order!!.sellerConfirm = true
                    order!!.deliveryInstruction=sellerInstruction.text.toString()
                    order!!.deliveryImages=getImageArrayFromImageViews()
                    OrderDao.updateOrder(order!!)
                        .addOnSuccessListener {
                            Log.d(TAG, "order updated!")
                            Toast.makeText(applicationContext, "order updated!", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnFailureListener {
                            Log.d(
                                TAG,
                                "order update failed ${it.localizedMessage}"
                            )
                        }
                })
            } else {
                if(order!!.clientConfirm && order!!.sellerConfirm){
                    orderDetailPaymentStatus.text = "Payment released toward seller account!"
                    order!!.status="Payment released toward seller account!"
                    cnfirmlyout.visibility=View.GONE
                    OrderDao.updateOrder(order!!)
                }
                binding.buyerInstrctnLayout.visibility = View.VISIBLE
                clientInstruction.text=order!!.deliveryInstruction
                confirmDlvryBtn.setOnClickListener(View.OnClickListener {
                    if(order!!.sellerConfirm)
                        orderDetailPaymentStatus.text = "Payment released toward seller account!"
                    order!!.clientConfirm = true
                    OrderDao.updateOrder(order!!)
                        .addOnSuccessListener { Log.d(TAG, "order updated!");Toast.makeText(applicationContext, "order updated!", Toast.LENGTH_SHORT).show() }
                        .addOnFailureListener { Log.d(TAG, "order update failed ${it.localizedMessage}") }
                })
            }
        }
    }


    fun addImageView(){
        val viewBinding= CustomViewImageBinding.inflate(layoutInflater)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 0, 10, 0)
        viewBinding.root.layoutParams=layoutParams

        binding.sellerDlvryInstrctnLayout.addView(viewBinding.root)
        imageViewsTable.put(viewBinding.hashCode(),viewBinding)
        viewBinding.insert.setOnClickListener(View.OnClickListener {
            photoPick(viewBinding.hashCode())
            addImageView()
        })
        viewBinding.delete.setOnClickListener(View.OnClickListener {
            binding.sellerDlvryInstrctnLayout.removeView(viewBinding.root)
            imagesList.remove(viewBinding.storeLink.text)
            if(!viewBinding.storeName.text.isNullOrBlank()) {
                StorageDao.deleteDeliveryImage(viewBinding.storeName.text.toString()).addOnFailureListener {
                    Log.d(FirebaseDao.TAG, "Delete failed:${it.localizedMessage}")
                }
            }
            viewBinding.storeLink.text=""
        })
        viewBinding.retry.setOnClickListener(View.OnClickListener {
            val imageUri= Uri.parse(viewBinding.storeUri.text.toString())
            CoroutineScope(Dispatchers.Default).launch { uploadImage(viewBinding,imageUri)}
            viewBinding.retry.visibility= View.INVISIBLE
//            binding.retryToast.visibility= View.INVISIBLE
            viewBinding.imagesViewImage.setImageURI(imageUri)
        })
    }
    fun photoPick(viewBindingHash:Int){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), viewBindingHash)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            Log.d(FirebaseDao.TAG,"onActivityResult: Failed")
            return
        }
        var imageUri=data!!.data
        Log.d(FirebaseDao.TAG,"onActivityResult Image received")
        var viewBinding = imageViewsTable.get(requestCode)!!
        viewBinding.imagesViewImage.setImageURI(imageUri)
        viewBinding.storeUri.setText(imageUri.toString())
        viewBinding.insert.visibility= View.INVISIBLE
        CoroutineScope(Dispatchers.Default).launch {
            uploadImage(viewBinding,imageUri!!)
        }
        return
    }
    suspend fun uploadImage(viewBinding: CustomViewImageBinding, imageUri: Uri) {
        progressBarFunc(viewBinding)
        val directory= Environment.getExternalStorageDirectory().absolutePath
        val imageFile= File("${directory}${imageUri.path?.replace("/document/primary:","/",true)}")
        val compressedImage = Compressor.compress(this, imageFile){
            default(720,1280, Bitmap.CompressFormat.JPEG,60)
        }
        Log.d(FirebaseDao.TAG,"orginalSize: ${imageFile.length()} compressedSize:${compressedImage.length()}")
        val fileName = imageUri.hashCode().toString()
        StorageDao.uploadDeliveryImage(compressedImage.toUri(), fileName)!!.addOnSuccessListener {
            Log.d(FirebaseDao.TAG,"upload success")
            StorageDao.getImageUrlOfGigs(fileName)!!.addOnSuccessListener {
                val imageLink=it.toString()
                viewBinding.storeLink.setText(imageLink)
                viewBinding.storeName.setText(fileName)
                Log.d(FirebaseDao.TAG,"getting Url success ${imageLink}")
            }
        }.addOnFailureListener {
            Log.d(FirebaseDao.TAG,"uploadImage onFailure: ${it.localizedMessage}")
            viewBinding.retry.visibility= View.VISIBLE
//            binding.retryToast.visibility= View.VISIBLE
            viewBinding.imagesViewImage.setImageResource(R.drawable.grey)
        }
    }
    private fun progressBarFunc(viewBinding: CustomViewImageBinding){
        var counter=0
        var timer= Timer()
        var timertask= timerTask {
            run(){
                super.runOnUiThread(Runnable {
                    viewBinding.progressBar.visibility= View.VISIBLE
                    counter++;
                    viewBinding.progressBar.setProgress(counter)
                    if(counter==100){
                        timer.cancel()
                        viewBinding.progressBar.visibility= View.INVISIBLE
                    }
                })
            }
        }
        timer.schedule(timertask,0,15)
    }
    fun getImageArrayFromImageViews():ArrayList<String>{
        var arr=ArrayList<String>()
        for(imageview in imageViewsTable.values){
            var s=imageview.storeLink.text.toString()
            if(!s.isNullOrBlank()){
                arr.add(s)
            }
        }
        return arr
    }
    override fun onBackPressed() {
        startActivity(Intent(this,Orders::class.java))
    }
}