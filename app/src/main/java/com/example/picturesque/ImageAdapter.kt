package com.example.picturesque

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(val context: Context) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private var imgUrls: List<FlickrPhoto>? = null

    private var listener : ImageClickListener? = null

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.iv_row_item_image)
    }

    fun setImgUrls(imgUrls: List<FlickrPhoto>) {
        this.imgUrls = imgUrls
        notifyDataSetChanged()
    }

    fun setImageClickListener(listener : ImageClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(context).inflate(R.layout.image_row_container, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val photo = imgUrls!![position]
        val serverId = photo.server
        val photoId = photo.id
        val secret = photo.secret
        val underscore = "_"
        val size = "w"
        val baseUrl = "https://live.staticflickr.com/"
        val imgUri = "$baseUrl/$serverId/$photoId$underscore$secret$underscore$size.jpg"
        photo.url = imgUri

        Glide.with(context)
            .load(imgUri)
            .into(holder.image)

        holder.image.setOnClickListener {
            listener!!.onClick(photo)
        }
    }

    override fun getItemCount(): Int {
        return imgUrls?.size ?: 0
    }
}

interface ImageClickListener{
    fun onClick(photo : FlickrPhoto)
}