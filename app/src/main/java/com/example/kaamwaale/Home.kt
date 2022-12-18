package com.example.kaamwaale

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kaamwaale.adapters.GigsAdapter
import com.example.kaamwaale.adapters.GigsFunctions
import com.example.kaamwaale.daos.FirebaseDao
import com.example.kaamwaale.daos.GigDao
import com.example.kaamwaale.models.Gig
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.razorpay.PaymentResultListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment(),GigsFunctions {
//    private var param1: String? = null
//    private var param2: String? = null
    lateinit var adapter:GigsAdapter
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
//    val context:MainActivity
//    init {
//        this.context=context
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v=inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView=v.findViewById<RecyclerView>(R.id.recyclerViewHome)
        recyclerView.layoutManager= GridLayoutManager(context,2, GridLayoutManager.VERTICAL,false)
        val query:Query=GigDao.gigCollection.orderBy("avgRating", Query.Direction.ASCENDING)
        val options: FirestoreRecyclerOptions<Gig> =FirestoreRecyclerOptions.Builder<Gig>().setQuery(query,Gig::class.java).build()
        adapter=GigsAdapter(options,this)
        recyclerView.adapter=adapter
        return v
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
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


//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment Home.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            Home().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}