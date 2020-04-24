package com.bese.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout

/**
 * 加载容器   可选择显示 加载view 内容view 错误view
 */
abstract class BaseLoadContainer(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {


    val mInflater: LayoutInflater = LayoutInflater.from(context)
    var onLoadListener: OnLoadListener? = null

    private var showing: View? = null
    private var contentView: View? = null
    abstract var loadingView: View
    abstract var emptyView: View
    abstract var errorView: View
    abstract var netErrorView: View

    override fun onFinishInflate() {
        super.onFinishInflate()
        // contentView
        contentView = getChildAt(0)
        showing = contentView
        // loadingView
        loadingView.visibility = View.GONE
        addView(loadingView)
        // emptyView
        emptyView.visibility = View.GONE
        addView(emptyView)
        // errorView
        errorView.visibility = View.GONE
        addView(errorView)
        // netErrorView
        netErrorView.visibility = View.GONE
        addView(netErrorView)
    }

    private fun show(toShow: View?) {
        if (showing != toShow) {
            showing?.visibility = View.GONE
            toShow?.visibility = View.VISIBLE
            showing = toShow
        }
    }

    fun showContent() {
        show(contentView)
    }

    open fun showLoading() {
        show(loadingView)
    }

    fun showEmpty() {
        show(emptyView)
    }

    fun showError() {
        show(errorView)
    }

    fun showNetError() {
        show(netErrorView)
    }

    interface OnLoadListener {
        fun onLoad()
    }

    fun load() {
        onLoadListener?.onLoad()
    }
}
