package com.example.kaamwaale.daos

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kaamwaale.models.Category
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object CategoryDao {
    var collection: CollectionReference = FirebaseDao.db.collection("category")
    var items=ArrayList<HashMap<String,Objects>>()
    var categoryHint=HashMap<String,Objects>()
    init {
        collection.document("categoryBucket").get().addOnSuccessListener {
            Log.d(FirebaseDao.TAG, "getCategory success")
            items = it.get("categoryBucket") as ArrayList<HashMap<String, Objects>>
        }

    }

}