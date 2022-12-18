package com.example.kaamwaale.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.borjabravo.simpleratingbar.SimpleRatingBar
import com.bumptech.glide.Glide
import com.example.kaamwaale.R
import com.example.kaamwaale.daos.FirebaseDao
import com.example.kaamwaale.daos.GigDao
import com.example.kaamwaale.models.Gig
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray
import com.mikhaellopez.circularimageview.CircularImageView


class GigsAdapter(options: FirestoreRecyclerOptions<Gig>,listener:GigsFunctions) :FirestoreRecyclerAdapter<Gig,GigsAdapter.ViewHolder>(options) {
     var listener:GigsFunctions
    init {
        this.listener=listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gigs, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Gig) {
        holder.headline.text=model.title
        Glide.with(holder.image.context).load(model.images[0]).into(holder.image)
        Glide.with(holder.userImage.context).load(model.userImage).into(holder.userImage)
        holder.userRating.text=model.avgRating.toString()
        var priceStarting=Integer.MAX_VALUE
        for(m in model.packages){
            if(m.price.toInt() < priceStarting) {
                priceStarting = m.price.toInt()
            }
        }
        holder.price.text=priceStarting.toString()
        holder.unit.text=model.packages[0].unit
        holder.userTotalRating.text=model.ratingCount.toString()
        holder.profession.text=model.profession
        holder.userName.text=model.name
        if(model.ratingCount.isNullOrBlank())
            holder.gigRating.rating=5F
        else
            holder.gigRating.rating=model.ratingCount.toFloat()
        holder.address.text=model.address

        val snapshots: ObservableSnapshotArray<Gig> = snapshots
        val gigId = snapshots.getSnapshot(holder.bindingAdapterPosition).id
        if(model.saveList.contains(FirebaseDao.auth.uid)){
            holder.save.setImageDrawable(ContextCompat.getDrawable(holder.save.getContext(),R.drawable.logo_saved))
        }else{
            holder.save.setImageDrawable(ContextCompat.getDrawable(holder.save.getContext(),R.drawable.logo_save))
        }
        holder.save.setOnClickListener(View.OnClickListener {
            if(model.saveList.contains(FirebaseDao.auth.uid)){
                GigDao.removeSaved(gigId)
                holder.save.setImageDrawable(ContextCompat.getDrawable(holder.save.getContext(),R.drawable.logo_save))
            }else{
                GigDao.saveGig(gigId)
                holder.save.setImageDrawable(ContextCompat.getDrawable(holder.save.getContext(),R.drawable.logo_saved))
            }
        })
        holder.hire.setOnClickListener(View.OnClickListener {
            listener.gigHire(model)
        })
        holder.root.setOnClickListener(View.OnClickListener { listener.gigClick(model) })
    }



    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        var image=view.findViewById<ImageView>(R.id.item_gigs_image)
        var headline=view.findViewById<TextView>(R.id.item_gigs_headline)
        var hire=view.findViewById<Button>(R.id.item_gigs_hire)
        var price=view.findViewById<TextView>(R.id.item_gigs_price)
        var save=view.findViewById<ImageButton>(R.id.item_gigs_save)
        var unit=view.findViewById<TextView>(R.id.item_gigs_price_unit)
        var profession=view.findViewById<TextView>(R.id.item_gigs_user_profession)
        var userImage=view.findViewById<CircularImageView>(R.id.item_gigs_user_image)
        var userRating=view.findViewById<TextView>(R.id.item_gigs_user_rating)
        var userTotalRating=view.findViewById<TextView>(R.id.item_gigs_user_totalRatings)
        var userName=view.findViewById<TextView>(R.id.item_gigs_user_name)
        var gigRating=view.findViewById<SimpleRatingBar>(R.id.item_gig_rating)
        var address=view.findViewById<TextView>(R.id.item_gig_address)
        var root=view.findViewById<ConstraintLayout>(R.id.item_gig_root_view)
    }
}
interface GigsFunctions{
//    fun gigSave(gigId:String)
    fun gigHire(gig:Gig)
    fun gigClick(gig:Gig)
}
