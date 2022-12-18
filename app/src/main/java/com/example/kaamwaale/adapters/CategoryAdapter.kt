package com.example.kaamwaale.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kaamwaale.R
import com.example.kaamwaale.Search
import java.util.*
import kotlin.collections.HashMap

class CategoryAdapter(fragment:Search):RecyclerView.Adapter<CategoryAdapter.ViewHolder>()  {

    lateinit var fragment: Search
    init {
        this.fragment=fragment
    }
    var items:ArrayList<HashMap<String,Objects>> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_category,parent,false)
        val viewHolder=ViewHolder(view)

        view.setOnClickListener(View.OnClickListener {
            fragment.onItemClicked(items.get(viewHolder.adapterPosition))
        })
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category= items.get(position)
        Glide.with(holder.itemView.context).load(category.get("pic").toString()).into(holder.image)
        holder.name.text= category.get("name").toString()
    }
    fun updateData(items: ArrayList<HashMap<String,Objects>>){
        this.items=items
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return items.size
    }
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var image:ImageFilterView=itemView.findViewById<ImageFilterView>(R.id.category_image)
        var name: TextView =itemView.findViewById<TextView>(R.id.category_name)
    }
}
