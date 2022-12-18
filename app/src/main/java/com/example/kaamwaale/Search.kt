package com.example.kaamwaale

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kaamwaale.adapters.CategoryAdapter
import com.example.kaamwaale.adapters.GigsAdapter
import com.example.kaamwaale.adapters.GigsAdapterSecond
import com.example.kaamwaale.adapters.GigsFunctions
import com.example.kaamwaale.daos.CategoryDao
import com.example.kaamwaale.daos.FirebaseDao.TAG
import com.example.kaamwaale.daos.GigDao
import com.example.kaamwaale.models.Gig
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class Search(mainActivity: MainActivity) : Fragment(),GigsFunctions{
     var query:Query=GigDao.gigCollection
    lateinit var recyclerView:RecyclerView
    lateinit var addressView:AutoCompleteTextView
    lateinit var sortSpinner:Spinner
    var mainActivity: MainActivity=mainActivity

    var catAdapter=CategoryAdapter(this)
    var backupStack: Stack<ArrayList<HashMap<String, Objects>>> = Stack()
    var gigArrayForDistance:ArrayList<Gig> = ArrayList()
    lateinit var addressAdapter:ArrayAdapter<String>
    lateinit var gigsAdapter: GigsAdapter
    lateinit var v:View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v=inflater.inflate(R.layout.fragment_search, container, false)

            recyclerView=v.findViewById<RecyclerView>(R.id.recyclerViewSearch)
            addressView=v.findViewById<AutoCompleteTextView>(R.id.search_Location)
            sortSpinner=v.findViewById(R.id.sort_spinner)
        recyclerviewDataEntry(CategoryDao.items)
            recyclerView.layoutManager= GridLayoutManager(context,2, GridLayoutManager.VERTICAL,false)
            recyclerView.adapter=catAdapter
            this.v=v
            Log.d(TAG,"finish")

        sortFunc()
        addressFunc()

        return v
    }
    fun sortFunc(){
        var arr= arrayListOf("Sort by","price","distance","rating")
        var arr2= arrayListOf("choose next!","5km","10km","50km","150km")
        var adapter=ArrayAdapter(context!!,R.layout.dropdown_item,arr)
        sortSpinner.adapter=adapter
        sortSpinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val s=sortSpinner.selectedItem as String

                when(s){
                    "price" ->{
                        firebaseAdapterCall(query.orderBy("price",Query.Direction.ASCENDING))
                    }
                    "distance" ->{
                        adapter.clear(); adapter.addAll(arr2);adapter.notifyDataSetChanged()
                    }
                    "rating" ->{
                        firebaseAdapterCall(query.orderBy("avgRating",Query.Direction.ASCENDING))
                    }
                    "5km"->{
                        funcForDistSearch(5000.0)
                    }
                    "10km"->{
                        funcForDistSearch(10000.0)
                    }
                    "50km"->{
                        funcForDistSearch(50000.0)
                    }
                    "150km"->{
                        funcForDistSearch(150000.0)
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.d(TAG,"")
            }
        }

    }

    private fun funcForDistSearch(radius: Double) {
        val addr=getAddressFromLocationName(addressView.text.toString())
        if(!addr.isNullOrEmpty()){
            geoQuery(radius,addr[0].longitude,addr[0].latitude)
        }
    }

    fun addressFunc(){
//        addressAdapter=ArrayAdapter(context!!,R.layout.dropdown_item,ArrayList<String>())
//        addressView.setAdapter(addressAdapter)
//        var textWatcher: TextWatcher =object: TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                Log.d("gg","hh")
//            }
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                Log.d("gg","hh")
//            }
//            override fun afterTextChanged(p0: Editable?) {
//                addressFunc2()
//            }
//        }
//        addressView.addTextChangedListener(textWatcher)
    }
    var arrBckp=ArrayList<String>()
    private fun addressFunc2(){
        CoroutineScope(Dispatchers.IO).launch {
            if(addressView.text.toString().length>2){
                var address=getAddressFromLocationName(addressView.text.toString())
                var arr=ArrayList<String>()
                if(!address.isNullOrEmpty()){
                    arr=changeAddressToStringArray(address)
                }
                if(arr.isNotEmpty() && !isSameArray(arr,arrBckp)){
                    mainActivity.runOnUiThread(Runnable {
                        arrBckp.addAll(arr)
                        addressAdapter.clear()
                        addressAdapter.addAll(arr)
                        addressAdapter.notifyDataSetChanged()
                    })
                }
            }
            else{
                mainActivity.runOnUiThread(Runnable {
                    addressAdapter.clear()
                    addressAdapter.notifyDataSetChanged()
                })
            }
        }

    }

    fun getAddressFromLocationName(locationName:String): List<Address> {
        var geocoder: Geocoder = Geocoder(context,Locale.getDefault())
        var addressList=geocoder.getFromLocationName(locationName,5)
        return addressList
    }
    private fun changeAddressToStringArray(address:List<Address>):ArrayList<String>{
        var arr=ArrayList<String>()
        for(it in address){
//            arr.add("${it.locality}, ${it.adminArea}, ${it.subAdminArea}")
            arr.add("${it.getAddressLine(0)}")
        }
        return arr
    }
    fun isSameArray(arr1:ArrayList<String>, arr2:ArrayList<String>): Boolean {
        arr1.toSortedSet()
        if(arr1.toSortedSet().toString() == arr2.toSortedSet().toString()){
            return true
        }
        return false
    }


    fun geoQuery(radiusInM:Double,lng:Double,lat:Double){
        val center = GeoLocation(lat, lng)
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {
            val q: Query = query.orderBy("hash").startAt(b.startHash).endAt(b.endHash)
            tasks.add(q.get())
        }
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                for (task in tasks) {
                    val snap = task.result
                    for (doc in snap.documents) {
                        val lat = doc.getDouble("lat")!!
                        val lng = doc.getDouble("lng")!!

                        // We have to filter out a few false positives due to GeoHash
                        // accuracy, but most will match
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radiusInM) {
                            matchingDocs.add(doc)
                        }
                    }
                }
                // matchingDocs contains the results
                gigArrayForDistance.clear()
                for(d in matchingDocs){
                    gigArrayForDistance.add(d.toObject(Gig::class.java)!!)
                }
                nonFirebaseAdapterCall()
            }
    }

    private fun recyclerviewDataEntry(items: ArrayList<HashMap<String, Objects>>) {
        catAdapter.updateData(items!!)
        backupStack.push(items)
    }


    fun onItemClicked(item: HashMap<String, Objects>) {
        val subcategory=item.get("subCategories") as ArrayList<HashMap<String,Objects>>
        if(subcategory.size > 0){
            recyclerviewDataEntry(subcategory)
        }else{
            query=GigDao.gigCollection.whereArrayContains("category",item["name"] as String)
            firebaseAdapterCall(query)
        }
    }
    fun nonFirebaseAdapterCall(){
        var adapter=GigsAdapterSecond(this)
        adapter.updateData(gigArrayForDistance)
        recyclerView.swapAdapter(adapter,true)
    }
    fun firebaseAdapterCall(query: Query){
        recyclerView.layoutManager= GridLayoutManager(context,2, GridLayoutManager.VERTICAL,false)
        val options: FirestoreRecyclerOptions<Gig> =
            FirestoreRecyclerOptions.Builder<Gig>().setQuery(query,Gig::class.java).build()
        gigsAdapter=GigsAdapter(options,this)
        recyclerView.adapter=gigsAdapter
        gigsAdapter.startListening()

    }
    fun onBackPressed(){
        if(backupStack.size>1){
            backupStack.pop() //a meaningless pop just as we need the second item from the stack
            catAdapter.updateData(backupStack.pop())
        }else if(backupStack.size==1){
            catAdapter.updateData(backupStack.pop())
        }else{

        }

    }

    override fun onStart() {
        super.onStart()
//        gigsAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
//        gigsAdapter.stopListening()
    }

    override fun gigHire(gig: Gig) {
        OrderDialog.processOrder(requireActivity(),requireContext(),gig,layoutInflater)
    }

    override fun gigClick(gig: Gig) {
        val gson = Gson()
        val intent = Intent(context, GigsDetail::class.java)
        intent.putExtra("gig", gson.toJson(gig))
        startActivity(intent)
    }


}