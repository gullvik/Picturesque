package com.example.picturesque

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageAdapter(val context: Context, val onClick: (FlickrPhoto) -> Unit) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private var images: List<FlickrPhoto>? = null

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.iv_row_item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(context).inflate(R.layout.image_row_container, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val photo = images!![position]
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
            .placeholder(R.drawable.ic_baseline_image_24)
            .into(holder.image)

        holder.image.setOnClickListener {
            onClick(images!![position])
        }
    }

    override fun getItemCount(): Int {
        return images?.size ?: 0
    }
}
