package com.example.kaamwaale.daos

import com.example.kaamwaale.models.Order
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

object OrderDao {
    val reference=FirebaseDao.db.collection("gigs")
    fun addOrder(order: Order): Task<Void> {
        return reference.document().set(order)
    }
    fun getOrder(orderId: String): Task<DocumentSnapshot> {
        return reference.document(orderId).get()
    }
    fun updateOrder(order: Order): Task<Void> {
        return reference.document(order.orderId).set(order)
    }

}