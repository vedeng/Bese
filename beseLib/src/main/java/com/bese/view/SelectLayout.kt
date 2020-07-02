package com.bese.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.bese.R
import com.google.android.flexbox.FlexboxLayout
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * 选择容器，自动换行
 *      默认子View全是TextView，如需要采用其他View，可继承自定义
 */
class SelectLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FlexboxLayout(context, attrs, defStyleAttr) {

    private var mMinSelectCount: Int = 0
    private var mMaxSelectCount: Int = 1
    private var mSelectedChildList: LinkedList<View> = LinkedList()
    var onSelectChangeListener: OnSelectChangeListener? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectLayout, defStyleAttr, 0)
        mMinSelectCount = typedArray.getInt(R.styleable.SelectLayout_minSelectCount, mMinSelectCount)
        mMaxSelectCount = typedArray.getInt(R.styleable.SelectLayout_maxSelectCount, mMaxSelectCount)
        fixSelectCount()
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
    fun resetChildren(@LayoutRes childLayoutRes: Int, data: List<String?>?) {
        // 不适用于子View过多的情况
        removeAllViews()
        data?.let {
            for (i in it.indices) {
                val child = LayoutInflater.from(context).inflate(childLayoutRes, this, false)
                addView(child)
                (child as? TextView)?.run {
                    text = it[i]
                    setOnClickListener {
                        if (isSelected) {
                            unSelectChild(this)
                            onSelectChangeListener?.onUnSelect(i, child)
                        } else {
                            selectChild(this)
                            onSelectChangeListener?.onSelect(i, child)
                        }
                    }
                }
            }
        }
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
    fun getSelectedChildren(): List<View> {
        return mSelectedChildList
    }

    /**
     * 修正SelectCount相关变量
     */
    private fun fixSelectCount() {
        mMinSelectCount = max(0, min(childCount, mMinSelectCount))
        mMaxSelectCount = min(childCount, max(mMinSelectCount, mMaxSelectCount))
    }

    private fun unSelectChild(child: View) {
        if (mSelectedChildList.size > mMinSelectCount) {
            child.isSelected = false
            mSelectedChildList.remove(child)
        }
    }

    private fun selectChild(child: View) {
        child.isSelected = true
        mSelectedChildList.addLast(child)

        if (mSelectedChildList.size > mMaxSelectCount) {
            mSelectedChildList.first.isSelected = false
            mSelectedChildList.removeFirst()
        }
    }

    fun setSelectListener(selectListener: OnSelectChangeListener?) {
        onSelectChangeListener = selectListener
    }

    interface OnSelectChangeListener {
        fun onSelect(int: Int, view: View)
        fun onSelectAll()

        fun onUnSelect(int: Int, view: View)
        fun onUnSelectAll()
    }
}