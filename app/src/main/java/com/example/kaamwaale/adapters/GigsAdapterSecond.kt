package com.example.kaamwaale.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kaamwaale.R
import com.example.kaamwaale.daos.GigDao
import com.example.kaamwaale.models.Gig
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class GigsAdapterSecond(listener:GigsFunctions) :RecyclerView.Adapter<GigsAdapter.ViewHolder>() {
    var items:ArrayList<Gig> = ArrayList()
    var listener:GigsFunctions
    init {
        this.listener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GigsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gigs, parent, false)
//        val viewHolder=ViewHolder(view)
//        view.setOnClickListener(View.OnClickListener {
//            listener.gigClick(items.get(viewHolder.adapterPosition))
//        })
        return GigsAdapter.ViewHolder(view)
    }

//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val model=items.get(position)
//        holder.headline.text=model.title
//        Glide.with(holder.image.context).load(model.images[0]).into(holder.image)
//        Glide.with(holder.userImage.context).load(model.userImage).into(holder.userImage)
//        holder.userRating.text=model.avgRating.toString()
//        var priceStarting=0
//        for(m in model.packages){
//            if(m.price.toInt()<priceStarting) priceStarting=m.price.toInt()
//        }
//        holder.price.text=priceStarting.toString()
//        holder.unit.text=model.packages[0].unit
//        holder.userTotalRating.text=model.ratingCount.toString()
//        holder.profession.text=model.profession
//        holder.userName.text=model.name
//        holder.gigRating.text=model.ratingCount.toString()
//        holder.address.text=model.address
//
////        val snapshots: ObservableSnapshotArray<Gig> = snapshots
////        val gigId = snapshots.getSnapshot(holder.bindingAdapterPosition).id
////        if(GigDao.gigIdArray.contains(gigId)){
////            holder.save.setImageDrawable(ContextCompat.getDrawable(holder.save.getContext(),R.drawable.logo_saved))
////        }else{
////            holder.save.setImageDrawable(ContextCompat.getDrawable(holder.save.getContext(),R.drawable.logo_save))
////        }
////        holder.save.setOnClickListener(View.OnClickListener {
////            if(GigDao.gigIdArray.contains(gigId)){
////                GigDao.removeSaved(gigId)
////                holder.save.setImageDrawable(ContextCompat.getDrawable(holder.save.getContext(),R.drawable.logo_save))
////            }else{
////                GigDao.saveGig(gigId)
////                holder.save.setImageDrawable(ContextCompat.getDrawable(holder.save.getContext(),R.drawable.logo_saved))
////            }
////        })
////        holder.hire.setOnClickListener(View.OnClickListener {
////            listener.gigHire(gigId)
////        })
//        holder.root.setOnClickListener(View.OnClickListener { listener.gigClick(model) })
//    }

    override fun getItemCount(): Int {
       return items.size
    }
    fun updateData(items: ArrayList<Gig>){
        this.items=items
        notifyDataSetChanged()
    }
//    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
//        var image=view.findViewById<ImageView>(R.id.item_gigs_image)
//        var headline=view.findViewById<TextView>(R.id.item_gigs_headline)
//        var hire=view.findViewById<Button>(R.id.item_gigs_hire)
//        var price=view.findViewById<TextView>(R.id.item_gigs_price)
//        var save=view.findViewById<ImageButton>(R.id.item_gigs_save)
//        var unit=view.findViewById<TextView>(R.id.item_gigs_price_unit)
//        var profession=view.findViewById<TextView>(R.id.item_gigs_user_profession)
//        var userImage=view.findViewById<ImageView>(R.id.item_gigs_user_image)
//        var userRating=view.findViewById<TextView>(R.id.item_gigs_user_rating)
//        var userTotalRating=view.findViewById<TextView>(R.id.item_gigs_user_totalRatings)
//        var userName=view.findViewById<TextView>(R.id.item_gigs_user_name)
//        var gigRating=view.findViewById<TextView>(R.id.item_gig_rating)
//        var address=view.findViewById<TextView>(R.id.item_gig_address)
//        var root=view.findViewById<ConstraintLayout>(R.id.item_gig_root_view)
//    }

    override fun onBindViewHolder(holder: GigsAdapter.ViewHolder, position: Int) {
        val model=items.get(position)
        holder.headline.text=model.title
        Glide.with(holder.image.context).load(model.images[0]).into(holder.image)
        Glide.with(holder.userImage.context).load(model.userImage).into(holder.userImage)
        holder.userRating.text=model.avgRating.toString()
        var priceStarting=0
        for(m in model.packages){
            if(m.price.toInt()<priceStarting) priceStarting=m.price.toInt()
        }
        holder.price.text=priceStarting.toString()
        holder.unit.text=model.packages[0].unit
        holder.userTotalRating.text=model.ratingCount.toString()
        holder.profession.text=model.profession
        holder.userName.text=model.name
        holder.gigRating.rating=model.ratingCount.toFloat()
        holder.address.text=model.address
        holder.root.setOnClickListener(View.OnClickListener { listener.gigClick(model) })
    }
}
