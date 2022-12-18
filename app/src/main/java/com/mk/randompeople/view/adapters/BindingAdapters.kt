package com.mk.randompeople.view.adapters

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.mk.randompeople.R

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView,url:String?){
    url?.let{
        val uri=it.toUri().buildUpon().scheme("https").build()
        Glide.with(imageView.context)
            .load(uri)
            .placeholder(R.drawable.app_icon)
            .into(imageView)
    }
}