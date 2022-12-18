package com.example.kaamwaale.daos

import android.util.Log
import com.example.kaamwaale.daos.FirebaseDao.TAG
import com.example.kaamwaale.models.Gig
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

object GigDao {
    var gigCollection=FirebaseDao.db.collection("gigs")
    fun addGig(gig: Gig): Task<Void> {
        var v= gigCollection.document().set(gig)
        Log.d(TAG, "add gig:success")
        return v
    }
    fun getGig(id:String): Task<DocumentSnapshot> {
        return gigCollection.document(id).get()
    }
    fun saveGig(id:String){
        getGig(id).addOnSuccessListener { document ->
            document.toObject(Gig::class.java)?.let { gig ->
                gig.saveList.add(FirebaseDao.auth.uid.toString())
                gigCollection.document(id).update("saveList",gig.saveList)
                    .addOnSuccessListener { Log.d(TAG, "gig save:success") }
                    .addOnFailureListener { Log.d(TAG, "gig save failed:${it.localizedMessage}") }
            }
        }.addOnFailureListener { Log.d(TAG,"gig fetch for saving failed:${it.localizedMessage}") }
    }

    fun removeSaved(id:String){
        getGig(id).addOnSuccessListener { document ->
            document.toObject(Gig::class.java)?.let { gig ->
                gig.saveList.remove(FirebaseDao.auth.uid.toString())
                gigCollection.document(id).update("saveList",gig.saveList)
                    .addOnSuccessListener { Log.d(TAG, "gig remove:success") }
                    .addOnFailureListener { Log.d(TAG, "gig remove failed:${it.localizedMessage}") }
            }
        }.addOnFailureListener { Log.d(TAG,"gig fetch for removing failed:${it.localizedMessage}") }
    }
}