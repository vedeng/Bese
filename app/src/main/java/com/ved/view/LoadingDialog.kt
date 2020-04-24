package com.ved.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialog
import androidx.core.widget.ContentLoadingProgressBar
import com.ved.R

/**
 * 全局的加载对话框
 */
class LoadingDialog(ctx: Context?, @StyleRes val theme: Int = R.style.Dialog_Fullscreen) : AppCompatDialog(ctx, theme) {

    private var anim: ContentLoadingProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)
        anim = findViewById(R.id.lc_loading_clpb)
        setCancelable(true)
        setCanceledOnTouchOutside(false)
        window?.setDimAmount(0f)
    }

    override fun show() {
        try {
            super.show()
            anim?.show()
        } catch (e: Exception) {
            Log.e("LoadDialog-show-Error", "${e.message}")
        }
    }

    override fun dismiss() {
        try {
            anim?.hide()
            super.dismiss()
        } catch (e: Exception) {
            Log.e("LoadDialog-hide-Error", "${e.message}")
        }
    }

}