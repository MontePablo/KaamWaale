package com.example.kaamwaale

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.os.IResultReceiver
import android.util.Log
import com.example.kaamwaale.daos.ChatDao
import com.example.kaamwaale.daos.FirebaseDao.TAG
import com.example.kaamwaale.models.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class Demo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)


    }
}