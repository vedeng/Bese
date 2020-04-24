package com.ved.glide

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ved.R
import com.youth.banner.loader.ImageLoader


class BannerGlideImageLoader : ImageLoader() {
    override fun displayImage(context: Context, path: Any, imageView: ImageView) {
        Glide.with(context).load(path).placeholder(R.drawable.svg_placeholder).into(imageView)
    }
}