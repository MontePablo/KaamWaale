package com.example.kaamwaale.daos

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

object StorageDao {
    val gigsImageRef = FirebaseStorage.getInstance().getReference("gigsImages")
    val delvryImageRef = FirebaseStorage.getInstance().getReference("deliveryImages")
    val profileImageRef = FirebaseStorage.getInstance().getReference("profileImages")
    fun uploadGigImage(imagePathUri: Uri?, fileName: String?): UploadTask? {
        Log.d("TAG", "storageDao uploadImage start")
        return gigsImageRef.child(fileName!!).putFile(imagePathUri!!)
    }
    fun uploadDeliveryImage(imagePathUri: Uri?, fileName: String?): UploadTask? {
        Log.d("TAG", "storageDao uploadImage start")
        return delvryImageRef.child(fileName!!).putFile(imagePathUri!!)
    }
    fun deleteGigImage(fileName: String?): Task<Void> {
        return gigsImageRef.child(fileName!!).delete()
    }
    fun deleteDeliveryImage(fileName: String?): Task<Void> {
        return delvryImageRef.child(fileName!!).delete()
    }

    fun getImageUrlOfGigs(filename: String?): Task<Uri?>? {
        return gigsImageRef.child(filename!!).downloadUrl
    }
    fun getImageUrlOfDelivery(filename: String?): Task<Uri?>? {
        return delvryImageRef.child(filename!!).downloadUrl
    }
    fun uploadProfileImage(imagePathUri: Uri?, fileName: String?): UploadTask? {
        Log.d("TAG", "storageDao uploadImage start")
        return profileImageRef.child(fileName!!).putFile(imagePathUri!!)
    }
    fun getImageUrlOfProfile(filename: String?): Task<Uri?>? {
        return profileImageRef.child(filename!!).downloadUrl
    }
}