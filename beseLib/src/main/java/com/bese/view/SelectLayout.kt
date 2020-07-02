package com.bese.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import com.bese.R
import com.google.android.flexbox.FlexboxLayout
import java.util.*
import kotlin.collections.ArrayList

/**
 * 选择容器，自动换行
 *      默认子View全是TextView，如需要采用其他View，可继承自定义
 */
class SelectLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FlexboxLayout(context, attrs, defStyleAttr) {

    private var mMinSelectCount: Int = 0
    private var mMaxSelectCount: Int = 1
    private var mSelectedChildList: LinkedList<View> = LinkedList()
    private var mViewList: ArrayList<View> = arrayListOf()
    private var onSelectChangeListener: OnSelectChangeListener? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectLayout, defStyleAttr, 0)
        mMinSelectCount = typedArray.getInt(R.styleable.SelectLayout_minSelectCount, mMinSelectCount)
        mMaxSelectCount = typedArray.getInt(R.styleable.SelectLayout_maxSelectCount, mMaxSelectCount)
        typedArray.recycle()
    }

    fun setMinSelectCount(count: Int) {
        mMinSelectCount = if (count < 0) 0 else count
        fixSelectCount()
    }

    fun setMaxSelectCount(count: Int) {
        mMaxSelectCount = if (count < 1) 1 else count
        fixSelectCount()
    }

    /**
     * 重置所有子View
     */
    fun initChild(@LayoutRes childRes: Int, childSize: Int) {
        // 不适用于子View过多的情况
        removeAllViews()
        mViewList = arrayListOf()
        for (i in 0 until childSize) {
            val child = LayoutInflater.from(context).inflate(childRes, this, false)
            addView(child)
            mViewList.add(child)
            child.setOnClickListener {
                if (isSelected) {
                    unSelectChild(child)
                    onSelectChangeListener?.onUnSelect(i, child)
                } else {
                    selectChild(child)
                    onSelectChangeListener?.onSelect(i, child)
                }
            }
        }
        // 适配数据长度后，检查修正min和max是否不符合规则
        fixSelectCount()
    }

    /**
     * 选中子View
     */
    fun selectChildOfIndex(index: Int) {
        selectChild(getChildAt(index))
    }

    /**
     * 取消选中子View
     */
    fun unSelectChildOfIndex(index: Int) {
        unSelectChild(getChildAt(index))
    }

    /**
     * 获取选中子view
     */
    fun getSelectedChildren(): LinkedList<View> {
        return mSelectedChildList
    }

    fun <T> getViewList() : ArrayList<T?> {
        val typeList = arrayListOf<T?>()
        mViewList.forEach {
            typeList.add(it as? T)
        }
        return typeList
    }

    /**
     * 修正SelectCount相关变量
     */
    private fun fixSelectCount() {
        if (mMinSelectCount < 0) mMinSelectCount = 0
        if (mMinSelectCount > mMaxSelectCount) {
            val t = mMaxSelectCount
            mMaxSelectCount = mMinSelectCount
            mMinSelectCount = t
        }
    }

    private fun unSelectChild(child: View) {
        if (mSelectedChildList.size > mMinSelectCount && child.isSelected) {
            child.isSelected = false
            mSelectedChildList.remove(child)
        }
    }

    private fun selectChild(child: View) {
        if (!child.isSelected) {
            child.isSelected = true
            mSelectedChildList.addLast(child)

            if (mSelectedChildList.size > mMaxSelectCount) {
                mSelectedChildList.first.isSelected = false
                mSelectedChildList.removeFirst()
            }
        }
    }

    fun setSelectListener(selectListener: OnSelectChangeListener?) {
        onSelectChangeListener = selectListener
    }

    interface OnSelectChangeListener {
        fun onSelect(pos: Int, view: View)
        fun onUnSelect(pos: Int, view: View)
    }

}