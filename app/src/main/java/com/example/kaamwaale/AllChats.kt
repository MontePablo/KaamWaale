//package com.example.kaamwaale
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import com.example.kaamwaale.adapters.AllChatAdapter
//import com.example.kaamwaale.adapters.Cfunc
//import com.example.kaamwaale.databinding.ActivityAllChatsBinding
//
//class AllChats : AppCompatActivity() , Cfunc {
//    lateinit var binding:ActivityAllChatsBinding
//    lateinit var adapter: AllChatAdapter
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding=ActivityAllChatsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        adapter=AllChatAdapter(this)
//
//    }
//
//    override fun chatClick(chatId: String) {
//
//    }
//}