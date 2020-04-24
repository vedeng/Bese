package com.ved.util.fragmentnav

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.ved.R

/**
 * <底部导航按钮管理布局>
 */
class BottomNavView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : LinearLayoutCompat(context, attrs, defStyleAttr) {

    var mOnNavClickListener: OnNavClickListener? = null

    private val homeIcon: ImageView
    private val categoryIcon: ImageView
    private val cartIcon: ImageView
    private val centerIcon: ImageView
    private val homeTxt: TextView
    private val categoryTxt: TextView
    private val cartTxt: TextView
    private val centerTxt: TextView
    private var LAST_POSITION = 0

    init {
        orientation = HORIZONTAL
        val view = View.inflate(context, R.layout.layout_bottom_navigator, this)
        homeIcon = view.findViewById(R.id.icon_tab_home)
        homeTxt = view.findViewById(R.id.tv_tab_home)
        categoryIcon = view.findViewById(R.id.icon_tab_category)
        categoryTxt = view.findViewById(R.id.tv_tab_category)
        cartIcon = view.findViewById(R.id.icon_tab_purchase)
        cartTxt = view.findViewById(R.id.tv_tab_purchase)
        centerIcon = view.findViewById(R.id.icon_tab_center)
        centerTxt = view.findViewById(R.id.tv_tab_center)
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            childView.setOnClickListener { v: View? ->
                mOnNavClickListener?.onNavClick(i, v)
            }
        }
    }
    interface OnNavClickListener {
        /**
         * 导航按钮点击事件
         * @param position 导航位置
         * @param view View
         */
        fun onNavClick(position: Int, view: View?)
    }

    fun select(position: Int) {
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            if (i == position) {
                if (LAST_POSITION != i) {
                    selectChild(v, true)
                    LAST_POSITION = i
                }
            } else {
                selectChild(v, false)
            }
        }
    }

    private fun selectChild(child: View, select: Boolean) {
        if (child is ViewGroup) {
            val group = child
            group.isSelected = select
            for (i in 0 until group.childCount) {
                selectChild(group.getChildAt(i), select)
            }
        } else {
            child.isSelected = select
        }
    }

    fun setOnNavClickListener(listener: OnNavClickListener?) {
        mOnNavClickListener = listener
    }

}