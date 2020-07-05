package com.bese.util

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.youth.banner.loader.ImageLoader

/**
 * Glide封装使用 继承了Banner控件接口
 */
class BannerGlideImageLoader : ImageLoader() {
    override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) {
        try {
            imageView?.run { Glide.with(this).load(path).into(this) }
        } catch (e: Exception) {
            Log.e("GlideImageLoader Error:", e.message + "")
        }
    }
}