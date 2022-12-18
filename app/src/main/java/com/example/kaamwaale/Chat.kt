package com.example.kaamwaale

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kaamwaale.adapters.ChatAdapter
import com.example.kaamwaale.adapters.ChatFunctions
import com.example.kaamwaale.daos.ChatDao
import com.example.kaamwaale.daos.FirebaseDao
import com.example.kaamwaale.daos.FirebaseDao.TAG
import com.example.kaamwaale.databinding.ActivityChatBinding
import com.example.kaamwaale.models.Chat
import com.example.kaamwaale.models.Message
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Chat : AppCompatActivity(),ChatFunctions {
    lateinit var receiverId:String
    lateinit var gigTitle:String
    lateinit var docId:String
    lateinit var adapter:ChatAdapter
    lateinit var binding : ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"chat on create")
        super.onCreate(savedInstanceState)
        receiverId= intent.getStringExtra("receiverId")!!
        gigTitle=intent.getStringExtra("gigName")!!
        docId=FirebaseDao.auth.uid+receiverId
        var rImage=intent.getStringExtra("receiverImage")
        var sImage=intent.getStringExtra("senderImage")
        val chat=Chat(); chat.run { senderImage=sImage!!;receiverImage=rImage!!;users.add(receiverId);users.add(FirebaseDao.auth.uid!!) }
        ChatDao.reference.document(docId).set(chat)
        binding= ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.sendMsg.setOnClickListener(View.OnClickListener { send() })
        if(!gigTitle.isNullOrBlank()){
            binding.msgExtraMsg.text="${binding.msgExtraMsg.text} '${gigTitle}'"
            binding.msgExtraMsg.visibility=View.VISIBLE
            binding.msgExtraMsg.setOnClickListener(View.OnClickListener { ChatDao.sendMsg(docId,createMsg(binding.msgExtraMsg.text.toString()))
                .addOnFailureListener { Log.d(FirebaseDao.TAG,"chat send:${it.localizedMessage}") }
                binding.msgExtraMsg.visibility=View.GONE
            })
        }
        binding.sendMsg.setOnClickListener(View.OnClickListener { send() })
        recyclerFunc()
        Log.d(TAG,"chat on finish")
    }
    fun recyclerFunc(){
        Log.d(TAG,"chat recyclerfunc")
        val recyclerOptions: FirestoreRecyclerOptions<Message> = FirestoreRecyclerOptions.Builder<Message>().setQuery(ChatDao.getQuery(docId),Message::class.java).build()
        adapter=ChatAdapter(recyclerOptions,this)
        binding.chatRecyclerview.adapter=adapter
        binding.chatRecyclerview.layoutManager=LinearLayoutManager(this)
        adapter.startListening()
    }

    override fun onStart() {
        super.onStart()
//        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
    }

    fun send(){
        var msg=binding.typeMsg.text.toString()
        if(!msg.isNullOrBlank()){
            ChatDao.sendMsg(docId,createMsg(msg))
                .addOnFailureListener { Log.d(FirebaseDao.TAG,"chat send:${it.localizedMessage}") }
        }else{
            Toast.makeText(this,"write something first!",Toast.LENGTH_SHORT).show()
        }
    }
    fun createMsg(s:String):Message{
        val calendar: Calendar = Calendar.getInstance() // Returns instance with current date and time set
        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val dateTime=formatter.format(calendar.time)
        val msg=Message()
        msg.message=s; msg.uid=FirebaseDao.auth.uid.toString();msg.dateTime=dateTime
        return msg
    }

    override fun scrolltoBottom(size: Int) {
        binding.chatRecyclerview.scrollToPosition(size-1)
    }

}
