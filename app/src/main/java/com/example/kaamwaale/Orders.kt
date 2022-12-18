package com.example.kaamwaale

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kaamwaale.adapters.OrderAdapter
import com.example.kaamwaale.adapters.OrderFunctions
import com.example.kaamwaale.daos.FirebaseDao
import com.example.kaamwaale.daos.GigDao
import com.example.kaamwaale.daos.OrderDao
import com.example.kaamwaale.databinding.ActivityOrdersBinding
import com.example.kaamwaale.models.Gig
import com.example.kaamwaale.models.Order
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.gson.Gson

class Orders : AppCompatActivity(),OrderFunctions {
    lateinit var binding:ActivityOrdersBinding
    lateinit var adapter:OrderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewOrder.layoutManager= LinearLayoutManager(this)
        val query: Query = OrderDao.reference.whereArrayContains("users",FirebaseDao.auth.uid.toString())
        val options: FirestoreRecyclerOptions<Order> = FirestoreRecyclerOptions.Builder<Order>().setQuery(query, Order::class.java).build()
        adapter= OrderAdapter(options,this)
        binding.recyclerViewOrder.adapter=adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun orderClick(order: Order) {
        val gson = Gson()
        val intent = Intent(applicationContext, OrderDetail::class.java)
        intent.putExtra("order", gson.toJson(order))
        startActivity(intent)
    }
    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
    }
}