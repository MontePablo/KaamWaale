package com.example.kaamwaale.adapters

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kaamwaale.R
import com.example.kaamwaale.daos.FirebaseDao
import com.example.kaamwaale.daos.UserDao
import com.example.kaamwaale.models.Chat
import com.example.kaamwaale.models.Gig
import com.example.kaamwaale.models.Message
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray


class AllChatAdapter(options: FirestoreRecyclerOptions<Chat>,listener:Cfunc) :FirestoreRecyclerAdapter<Chat,AllChatAdapter.ViewHolder>(options) {
    lateinit var listener:Cfunc
    init {
        this.listener=listener
    }
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        var image=view.findViewById<ImageView>(R.id.allchat_image)
        var root=view.findViewById<ConstraintLayout>(R.id.allchat_root)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_all_chats, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Chat) {
        if(model.senderImage==UserDao.user.pic){
            Glide.with(holder.image.context).load(model.receiverImage).into(holder.image)
        }else{
            Glide.with(holder.image.context).load(model.senderImage).into(holder.image)
        }
        val snapshots: ObservableSnapshotArray<Chat> = snapshots
        val id = snapshots.getSnapshot(holder.bindingAdapterPosition).id
        holder.root.setOnClickListener(View.OnClickListener { listener.chatClick(id) })
    }
}
interface Cfunc{
    fun chatClick(chatId:String)
}
