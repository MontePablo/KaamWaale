package com.example.kaamwaale

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kaamwaale.adapters.GigsAdapter
import com.example.kaamwaale.adapters.GigsFunctions
import com.example.kaamwaale.daos.FirebaseDao
import com.example.kaamwaale.daos.GigDao
import com.example.kaamwaale.databinding.ActivityMyGigsBinding
import com.example.kaamwaale.models.Gig
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.gson.Gson

class MyGigs : AppCompatActivity() ,GigsFunctions{
    lateinit var binding:ActivityMyGigsBinding
    lateinit var adapter:GigsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMyGigsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager= GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false)
        val query: Query = GigDao.gigCollection.whereEqualTo("uid",FirebaseDao.auth.uid)
        val options: FirestoreRecyclerOptions<Gig> = FirestoreRecyclerOptions.Builder<Gig>().setQuery(query, Gig::class.java).build()
        adapter= GigsAdapter(options,this)
        binding.recyclerView.adapter=adapter
        binding.flatingButton.setOnClickListener(View.OnClickListener { startActivity(Intent(this,CreateGigs::class.java)) })

    }

    override fun gigHire(gig: Gig) {
        OrderDialog.processOrder(this,this,gig,layoutInflater)
    }

    override fun gigClick(gig: Gig) {
        val gson = Gson()
        val intent = Intent(this, GigsDetail::class.java)
        intent.putExtra("gig", gson.toJson(gig))
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
    }
}