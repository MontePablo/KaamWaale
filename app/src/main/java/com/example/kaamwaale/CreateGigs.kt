package com.example.kaamwaale

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.example.kaamwaale.daos.CategoryDao
import com.example.kaamwaale.daos.FirebaseDao.TAG
import com.example.kaamwaale.daos.GigDao
import com.example.kaamwaale.daos.StorageDao
import com.example.kaamwaale.daos.UserDao
import com.example.kaamwaale.databinding.*
import com.example.kaamwaale.models.Gig
import com.example.kaamwaale.models.Package
import com.example.kaamwaale.models.User
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask

@RequiresApi(Build.VERSION_CODES.R)
class CreateGigs : AppCompatActivity() {

    var imagesList=ArrayList<String>()
    lateinit var binding:ActivityCreateGigsBinding
    lateinit var arrayAdapter:ArrayAdapter<String>
    var packageViewsArray:ArrayList<CustomViewPackageCreateBinding> = ArrayList<CustomViewPackageCreateBinding>()
    var categorySpinnerArray:ArrayList<CustomViewCategorySpinnerBinding> = ArrayList<CustomViewCategorySpinnerBinding>()

    var imageViewsTable:Hashtable<Int,CustomViewImageBinding> = Hashtable<Int,CustomViewImageBinding>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCreateGigsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)

        binding.currentLocation.setOnClickListener(View.OnClickListener { currentLocation() })
        arrayAdapter=ArrayAdapter(applicationContext,R.layout.dropdown_item,ArrayList<String>())
        binding.address.setAdapter(arrayAdapter)
        binding.publish.setOnClickListener(View.OnClickListener { uploadGig() })

        var textWatcher:TextWatcher=object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("gg","hh")
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("gg","hh")
            }
            override fun afterTextChanged(p0: Editable?) {
                    if(p0!!.length%3==0){
                        addressFunc()
                    }
            }
        }
        binding.address.addTextChangedListener(textWatcher)
        addPackageView()
        addImageView()
        binding.addPackage.setOnClickListener(View.OnClickListener { addPackageView() })
        addCategoryView(CategoryDao.items)
    }
    fun uploadGig(){
        var serviceType=""
        if(binding.serviceTypeRadioGroup.checkedRadioButtonId==binding.offline.id){
            serviceType="offline"
        }else{
            serviceType="online"
        }
        var title=binding.title.text.toString()
        var desc=binding.description.text.toString()
        var category=getStringArrayFromCatViews()
        var packages=getPackageArrayFromPackViews()
        var images=getImageArrayFromImageViews()
        val adrs=CoroutineScope(Default).run { getAddressFromLocationName(binding.address.text.toString()) }
        val address=binding.address.text.toString()
        var lng=adrs[0].longitude;var lat=adrs[0].latitude
        var hash= GeoFireUtils.getGeoHashForLocation(GeoLocation(lat, lng))
        var gig= Gig();
        gig.images=images;gig.title=title;gig.category=category
        gig.packages=packages;gig.serviceType=serviceType;gig.description=desc
        gig.lng=lng;gig.lat=lat;gig.hash=hash;gig.address=address
        val user=UserDao.user
        gig.uid=user.id;gig.profession=user.profession;gig.name=user.name
        gig.userAvgRating=user.userAvgRating;gig.userRatingCount=user.userRatingCount
        gig.skills=user.skills;gig.userImage=user.pic


        GigDao.addGig(gig)
            .addOnSuccessListener { Log.d(TAG,"gigUpload success"); Toast.makeText(this,"sucess",Toast.LENGTH_SHORT).show()
//                        startActivity(Intent(this,MainActivity::class.java))
//                        finish()
            }
            .addOnFailureListener { Log.d(TAG,"gigUpload failed:${it.localizedMessage}");Toast.makeText(this,"failed! retry later",Toast.LENGTH_SHORT).show()}
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
    fun getPackageArrayFromPackViews():ArrayList<Package>{
        var arr=ArrayList<Package>()
        for(pack in packageViewsArray){
            var title=pack.title.text.toString()
            var price=pack.price.text.toString()
            var desc=pack.description.text.toString()
            var unit:String
            if(pack.radioGroupPriceType.checkedRadioButtonId==pack.perDay.id){
                unit="Day"
            }else if(pack.radioGroupPriceType.checkedRadioButtonId==pack.perProject.id){
                unit="project"
            }else if(pack.radioGroupPriceType.checkedRadioButtonId==pack.perMonth.id){
                unit="month"
            }else{
                unit="hour"
            }
            val p=Package()
            p.title=title;p.unit=unit;p.price=price;p.description=desc
            arr.add(p)
        }
        return arr
    }
    fun getStringArrayFromCatViews():ArrayList<String>{
        var arr=ArrayList<String>()
        for(cat in categorySpinnerArray){
            var s=cat.spinner.selectedItem.toString()
            if(!s.isNullOrBlank()){
                arr.add(s)
            }
        }
        return arr
    }

    fun addCategoryView(categories:ArrayList<HashMap<String,Objects>>){
        categories.add(0,CategoryDao.categoryHint)
        val viewBinding=CustomViewCategorySpinnerBinding.inflate(layoutInflater)
        binding.linearLayoutCategory.addView(viewBinding.root)
        categorySpinnerArray.add(viewBinding)
        viewBinding.spinner.adapter=getCategoriesAsAdapter(categories)
        viewBinding.spinner.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val category=categories.get(p2)
                if(category.get("subCategories")!=null ){
                    val subCat=category.get("subCategories") as ArrayList<HashMap<String, Objects>>
                    if(subCat.size>0){
                        addCategoryView(subCat)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.d(TAG,"")
            }
        }
    }

    fun getCategoriesAsAdapter(categories:ArrayList<HashMap<String,Objects>>): ArrayAdapter<String> {
        var arr=ArrayList<String>()

        for ( category in categories){
            arr.add(category.get("name").toString())
        }
        val adapter=ArrayAdapter<String>(applicationContext,R.layout.dropdown_item,arr)
        return adapter
    }
    fun addImageView(){
        val viewBinding=CustomViewImageBinding.inflate(layoutInflater)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, 0, 10, 0)
        viewBinding.root.layoutParams=layoutParams

        binding.linearLayoutImages.addView(viewBinding.root)
        imageViewsTable.put(viewBinding.hashCode(),viewBinding)
        viewBinding.insert.setOnClickListener(View.OnClickListener {
            photoPick(viewBinding.hashCode())
            addImageView()
        })
        viewBinding.delete.setOnClickListener(View.OnClickListener {
            binding.linearLayoutImages.removeView(viewBinding.root)
            imagesList.remove(viewBinding.storeLink.text)
            if(!viewBinding.storeName.text.isNullOrBlank()) {
                StorageDao.deleteGigImage(viewBinding.storeName.text.toString()).addOnFailureListener {
                    Log.d(TAG, "Delete failed:${it.localizedMessage}")
                }
            }
            viewBinding.storeLink.text=""
        })
        viewBinding.retry.setOnClickListener(View.OnClickListener {
            val imageUri=Uri.parse(viewBinding.storeUri.text.toString())
            CoroutineScope(Default).launch { uploadImage(viewBinding,imageUri)}
            viewBinding.retry.visibility=View.INVISIBLE
            binding.retryToast.visibility=View.INVISIBLE
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
            Log.d(TAG,"onActivityResult: Failed")
            return
        }
        var imageUri=data!!.data
        Log.d(TAG,"onActivityResult Image received")
        var viewBinding = imageViewsTable.get(requestCode)!!
        viewBinding.imagesViewImage.setImageURI(imageUri)
        viewBinding.storeUri.setText(imageUri.toString())
        viewBinding.insert.visibility=View.INVISIBLE
        CoroutineScope(Default).launch {
            uploadImage(viewBinding,imageUri!!)
        }
        return
    }
    suspend fun uploadImage(viewBinding: CustomViewImageBinding,imageUri: Uri) {
        progressBarFunc(viewBinding)
        val directory=Environment.getExternalStorageDirectory().absolutePath
        val imageFile=File("${directory}${imageUri.path?.replace("/document/primary:","/",true)}")
        val compressedImage = Compressor.compress(this, imageFile){
            default(720,1280,Bitmap.CompressFormat.JPEG,60)
        }
        Log.d(TAG,"orginalSize: ${imageFile.length()} compressedSize:${compressedImage.length()}")
        val fileName = imageUri.hashCode().toString()
         StorageDao.uploadGigImage(compressedImage.toUri(), fileName)!!.addOnSuccessListener {
             Log.d(TAG,"upload success")
             StorageDao.getImageUrlOfGigs(fileName)!!.addOnSuccessListener {
                 val imageLink=it.toString()
                 viewBinding.storeLink.setText(imageLink)
                 viewBinding.storeName.setText(fileName)
                 Log.d(TAG,"getting Url success ${imageLink}")
             }
         }.addOnFailureListener {
             Log.d(TAG,"uploadImage onFailure: ${it.localizedMessage}")
             viewBinding.retry.visibility=View.VISIBLE
             binding.retryToast.visibility=View.VISIBLE
             viewBinding.imagesViewImage.setImageResource(R.drawable.grey)
         }
    }
    private fun progressBarFunc(viewBinding: CustomViewImageBinding){
        var counter=0
        var timer=Timer()
        var timertask=timerTask {
            run(){
                super.runOnUiThread(Runnable {
                    viewBinding.progressBar.visibility=View.VISIBLE
                    counter++;
                    viewBinding.progressBar.setProgress(counter)
                    if(counter==100){
                        timer.cancel()
                        viewBinding.progressBar.visibility=View.INVISIBLE
                    }
                })
            }
        }
        timer.schedule(timertask,0,15)
    }
    fun addPackageView(){
        val viewBinding=CustomViewPackageCreateBinding.inflate(layoutInflater)
        binding.linearLayoutPackages.addView(viewBinding.root)
        packageViewsArray.add(viewBinding)
        viewBinding.remove.setOnClickListener(View.OnClickListener {
            binding.linearLayoutPackages.removeView(viewBinding.root)
            packageViewsArray.remove(viewBinding)
        })
    }
    var arrr=ArrayList<String>()
    private fun addressFunc(){
        CoroutineScope(IO).launch {
            if(binding.address.text.toString().length>4){
                var address=getAddressFromLocationName(binding.address.text.toString())
                var arr=ArrayList<String>()
                if(!address.isNullOrEmpty()){
                    arr=changeAddressToStringArray(address)
                }
                if(arr.isNotEmpty() && !isSameArray(arr,arrr)){
                    super.runOnUiThread(Runnable {
                        arrr.addAll(arr)
                        arrayAdapter.clear()
                        arrayAdapter.addAll(arr)
                        arrayAdapter.notifyDataSetChanged()
                        })
                }
            }
            else{
                super.runOnUiThread(Runnable {
                arrayAdapter.clear()
                arrayAdapter.notifyDataSetChanged() })
            }
        }

    }
    fun isSameArray(arr1:ArrayList<String>, arr2:ArrayList<String>): Boolean {
        arr1.toSortedSet()
        if(arr1.toSortedSet().toString() == arr2.toSortedSet().toString()){
            return true
        }
        return false
    }
    private fun changeAddressToStringArray(address:List<Address>):ArrayList<String>{
        var arr=ArrayList<String>()
        for(it in address){
//            arr.add("${it.locality}, ${it.adminArea}, ${it.subAdminArea}")
            arr.add("${it.getAddressLine(0)}")
        }
        return arr
    }
    private fun currentLocation() {
        if(checkLocationPermission()){
          if(isLocationEnabled()){
              getLocationFromFusionProvider()
          }else{
              Toast.makeText(this,"Turn on Location first",Toast.LENGTH_SHORT).show()
              startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
          }
        } else{askLocationPermission()}
    }

    @SuppressLint("MissingPermission")
    private fun getLocationFromFusionProvider() {
        fusedLocationProviderClient.locationAvailability.addOnCompleteListener(this){
            Log.d(TAG,"locationAvaility: "+it.result.isLocationAvailable.toString())
        }
        fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){
            val location=it.result
            if(location==null)
                Log.d(TAG,"null location received")
            else
                updateUI(location)
        }.addOnFailureListener {
            Log.d(TAG,"exeption on fusedLocationProvider: ${it.localizedMessage}")
        }
    }
    private fun updateUI(location: Location){
        var address=getAddressFromLongLat(location)[0]
        binding.address.setText("${address.locality}, ${address.adminArea} ${address.postalCode}, ${address.countryName}")
        Log.d(TAG,"${address.getAddressLine(0)} @@ ${address.getAddressLine(1)}")
    }

     fun getAddressFromLocationName(locationName:String): List<Address> {
         var geocoder:Geocoder=Geocoder(this,Locale.getDefault())
        var addressList=CoroutineScope(Default).let {
            geocoder.getFromLocationName(locationName, 2)
        }
        return addressList
    }
    private fun getAddressFromLongLat(location:Location): List<Address> {
        CoroutineScope(IO).run {
        var geocoder:Geocoder=Geocoder(applicationContext, Locale.getDefault())
        var addressList = geocoder.getFromLocation(location.latitude,location.longitude,5)
        return addressList }
    }
    private fun isLocationEnabled():Boolean{
        val locationManger:LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManger.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun checkLocationPermission(): Boolean {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }
    private fun askLocationPermission(){
        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),123)
    }
    override
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==123){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.d(TAG,"Granted")
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
                currentLocation()
            }else{
                Log.d(TAG,"Denied")
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
    }
}


