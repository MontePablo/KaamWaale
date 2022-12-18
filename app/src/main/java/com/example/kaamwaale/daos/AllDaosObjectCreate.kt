package com.example.kaamwaale.daos

import android.util.Log
import com.example.kaamwaale.daos.FirebaseDao.TAG
import com.example.kaamwaale.daos.FirebaseDao.db
import com.google.firebase.firestore.auth.User
import java.util.*

object AllDaosObjectCreate {
    init {
        CategoryDao
        FirebaseDao
        StorageDao
        UserDao
//        calculateAverageRating()
    }
//    fun calculateAverageRating(){
//        UserDao.getUser(FirebaseDao.auth.uid!!).addOnSuccessListener {
//            val user=it.toObject(com.example.kaamwaale.models.User::class.java)
//            var count=-1; var sum=0.0;
//            for(rating in user!!.ratings){
//                val star=rating["star"] as Double
//                sum+=star
//                count++
//            }
//            var average=sum/count
//            if(average>user.avgRating)
//                user.avgRating=average
//                UserDao.addUser(user).addOnFailureListener { Log.d(TAG,"userupload:${it.localizedMessage}") }
//        }
//    }

}