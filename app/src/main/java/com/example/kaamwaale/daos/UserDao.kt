package com.example.kaamwaale.daos

import android.util.Log
import com.example.kaamwaale.daos.FirebaseDao.TAG
import com.example.kaamwaale.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot

object UserDao {
     var user:User
    var collection:CollectionReference
    init {
        collection = FirebaseDao.db.collection("users")
        user=User()
        init()
    }
//    var collection:CollectionReference = FirebaseDao.db.collection("users")
    fun addUser(user: User): Task<Void> {
        var v= collection.document(user.id).set(user)
        Log.d(FirebaseDao.TAG, "add user:success")
        v.addOnSuccessListener { init() }
        return v
    }
    fun getUser(userId:String): Task<DocumentSnapshot> {
        return collection.document(userId).get()
    }
    private fun init(){
        getUser(FirebaseDao.auth.uid!!).addOnSuccessListener {document ->
            document.toObject(User::class.java)?.let { user ->
                this.user=user
            }
        }.addOnFailureListener {
            Log.d(TAG,"user fetch failed:${it.localizedMessage}")
        }
    }

}