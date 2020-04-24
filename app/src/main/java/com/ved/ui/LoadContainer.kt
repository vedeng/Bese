package com.ved.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.ved.R
import com.bese.view.BaseLoadContainer
import kotlinx.android.synthetic.main.lc_error.view.*
import kotlinx.android.synthetic.main.lc_error_network.view.*

/**
 * 自定义加载容器
 */
class LoadContainer(context: Context, attrs: AttributeSet?) : BaseLoadContainer(context, attrs) {
    override var loadingView: View = mInflater.inflate(R.layout.loading, this, false)
        set(value) {
            removeView(field)
            field = value
            addView(value)
            value.visibility = View.GONE
        }
    override var emptyView: View = mInflater.inflate(R.layout.lc_empty, this, false)
        set(value) {
            removeView(field)
            field = value
            addView(value)
            value.visibility = View.GONE
        }
    override var errorView: View = mInflater.inflate(R.layout.lc_error, this, false)
        set(value) {
            removeView(field)
            field = value
            addView(value)
            value.visibility = View.GONE
        }
    override var netErrorView: View = mInflater.inflate(R.layout.lc_error_network, this, false)
        set(value) {
            removeView(field)
            field = value
            addView(value)
            value.visibility = View.GONE
        }

    init {
//        ((loadingView as ImageView).drawable as AnimationDrawable).start()
        errorView.lc_error_refresh.setOnClickListener { onLoadListener?.onLoad() }
        netErrorView.lc_error_network_refresh.setOnClickListener { onLoadListener?.onLoad() }
    }
}