package com.example.kaamwaale

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kaamwaale.GigsDetail
import com.example.kaamwaale.ProfileBusiness
import com.example.kaamwaale.R
import com.example.kaamwaale.adapters.GigsAdapter
import com.example.kaamwaale.adapters.GigsFunctions
import com.example.kaamwaale.daos.FirebaseDao
import com.example.kaamwaale.daos.GigDao
import com.example.kaamwaale.models.Gig
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.gson.Gson

class Save : Fragment(),GigsFunctions {
    lateinit var adapter:GigsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v= inflater.inflate(R.layout.fragment_save, container, false)
        val recyclerView=v.findViewById<RecyclerView>(R.id.recyclerViewSave)
        recyclerView.layoutManager= GridLayoutManager(context,2, GridLayoutManager.VERTICAL,false)
        val query: Query = GigDao.gigCollection.whereArrayContains("saveList",FirebaseDao.auth.uid.toString())
        val options: FirestoreRecyclerOptions<Gig> = FirestoreRecyclerOptions.Builder<Gig>().setQuery(query, Gig::class.java).build()
        adapter= GigsAdapter(options,this)
        recyclerView.adapter=adapter
        return v
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
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}