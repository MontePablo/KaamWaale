package com.example.kaamwaale.daos

import android.icu.number.IntegerWidth
import android.util.Log
import com.example.kaamwaale.daos.FirebaseDao.TAG
import com.example.kaamwaale.models.Message
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max
import kotlin.math.min

object ChatDao {
    val reference: CollectionReference = FirebaseDao.db.collection("chat")

    fun getMsgBoxId(receiverId:String,senderId:String): String {
        val id=senderId+receiverId
        val map=HashMap<String, Objects>()
//        map.put("id",id as Objects)
//        val users=arrayListOf(receiverId,senderId)
//        map.put("users",users as Objects)
        Log.d(TAG,"msgboxId started id: $id")

//        reference.document(id).set(map).addOnFailureListener { Log.d(TAG,"chat users upload:${it.localizedMessage}") }
        return id
    }
    fun sendMsg(docId: String, msg: Message): Task<Void> {
        return reference.document(docId).collection("messages").document().set(msg)
    }
    fun getQuery(docId:String):Query{
        return reference.document(docId).collection("messages").orderBy("dateTime",Query.Direction.ASCENDING)
    }
}