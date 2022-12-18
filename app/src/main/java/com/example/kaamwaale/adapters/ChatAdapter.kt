package com.example.kaamwaale.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kaamwaale.R
import com.example.kaamwaale.daos.FirebaseDao
import com.example.kaamwaale.models.Message
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class ChatAdapter(options: FirestoreRecyclerOptions<Message>,listener:ChatFunctions) :FirestoreRecyclerAdapter<Message,ChatAdapter.ViewHolder>(options) {
    lateinit var listener:ChatFunctions
    init {
        this.listener=listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Message) {
        if(model.uid==FirebaseDao.auth.uid){
            disableReceive(holder)
            holder.chatSend.text=model.message
            holder.timeSend.text=model.dateTime
        }else{
            disableSend(holder)
            holder.chatReceive.text=model.message
            holder.timeRecieve.text=model.dateTime
        }
        listener.scrolltoBottom(snapshots.size)
    }
    fun disableSend(holder:ViewHolder){
        holder.chatSend.visibility=View.GONE
        holder.timeSend.visibility=View.GONE
    }
    fun disableReceive(holder:ViewHolder){
        holder.chatReceive.visibility=View.GONE
        holder.timeRecieve.visibility=View.GONE
    }
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        var chatSend=view.findViewById<TextView>(R.id.chat_send)
        var chatReceive=view.findViewById<TextView>(R.id.chat_receive)
        var timeSend=view.findViewById<TextView>(R.id.timeSend)
        var timeRecieve=view.findViewById<TextView>(R.id.timeReceive)
    }
}
interface ChatFunctions{
    fun scrolltoBottom(size: Int)
}
