package com.example.kaamwaale.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.kaamwaale.R
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapter(images:ArrayList<String>): SliderViewAdapter<SliderAdapter.ViewHolder>() {
    var images:ArrayList<String> =ArrayList()
    init {
        this.images=images
    }
    override fun getCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_slider, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, position: Int) {
        Glide.with(viewHolder?.imageView!!.context).load(images[position]).into(viewHolder?.imageView!!)
    }
    class ViewHolder(itemView: View): SliderViewAdapter.ViewHolder(itemView){
        var imageView=itemView.findViewById<ImageView>(R.id.slide_image)
    }
}