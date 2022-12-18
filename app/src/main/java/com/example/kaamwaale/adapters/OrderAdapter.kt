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
import com.example.kaamwaale.Orders
import com.example.kaamwaale.R
import com.example.kaamwaale.daos.GigDao
import com.example.kaamwaale.models.Gig
import com.example.kaamwaale.models.Order
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray


class OrderAdapter(options: FirestoreRecyclerOptions<Order>,listener:OrderFunctions) :FirestoreRecyclerAdapter<Order,OrderAdapter.ViewHolder>(options) {
    var listener:OrderFunctions
    init {
        this.listener=listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Order) {

        val snapshots: ObservableSnapshotArray<Order> = snapshots
        val orderId = snapshots.getSnapshot(holder.bindingAdapterPosition).id
        model.orderId=orderId

        holder.title.text=model.title
        holder.review.setOnClickListener(View.OnClickListener {  })
        holder.status.text=model.status
        holder.dateTime.text=model.dateTime
        Glide.with(holder.image.context).load(model.image).into(holder.image)
        holder.root.setOnClickListener(View.OnClickListener {listener.orderClick(model)})
        holder.quantity.text=model.quantity

    }



    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        var image=view.findViewById<ImageView>(R.id.item_order_image)
        var dateTime=view.findViewById<TextView>(R.id.item_order_dateTime)
        var review=view.findViewById<Button>(R.id.item_order_review)
        var status=view.findViewById<TextView>(R.id.item_order_status)
        var title=view.findViewById<TextView>(R.id.item_order_title)
        var root=view.findViewById<ConstraintLayout>(R.id.item_order_root_view)
        var quantity=view.findViewById<TextView>(R.id.item_order_quantity)
    }
}
interface OrderFunctions{
    fun orderClick(order:Order)
}
