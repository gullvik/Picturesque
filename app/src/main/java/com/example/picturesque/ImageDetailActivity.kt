package com.example.picturesque

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.picturesque.Constants.IMAGE_TITLE
import com.example.picturesque.Constants.IMAGE_URI

class ImageDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_detail)

        val image: ImageView = findViewById(R.id.iv_photo_detail)

        val title: TextView = findViewById(R.id.tv_title)
        title.text = intent.getStringExtra(IMAGE_TITLE)

        Glide.with(this)
            .load(intent.getStringExtra(IMAGE_URI))
            .onlyRetrieveFromCache(true)
            .into(image)
    }
}