package com.bese.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import com.bese.R
import com.blankj.utilcode.util.ColorUtils
import com.google.android.flexbox.FlexboxLayout
import java.util.*
import kotlin.collections.ArrayList

/**
 * 选择容器，自动换行
 *      默认子View全是TextView，如需要采用其他View，可继承自定义
 */
class SelectTextLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FlexboxLayout(context, attrs, defStyleAttr) {

    private var mLayout: Int = R.layout.item_select_label
    private var selectStyle: Int = R.drawable.shape_select_check
    private var normalStyle: Int = R.drawable.shape_select_narmal
    private var disableStyle: Int = R.drawable.shape_select_disable
    private var selectTextColor: Int = Color.parseColor("#FFFFFF")
    private var normalTextColor: Int = Color.parseColor("#333333")
    private var disableTextColor: Int = Color.parseColor("#999999")

    private var mMinSelectCount: Int = 0
    private var mMaxSelectCount: Int = 1
    private var mSelectedChildList: LinkedList<TextView> = LinkedList()
    private var mViewList: ArrayList<TextView> = arrayListOf()
    private var onSelectChangeListener: OnSelectChangeListener? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectTextLayout, defStyleAttr, 0)
        mMinSelectCount = typedArray.getInt(R.styleable.SelectTextLayout_minSelectCount, mMinSelectCount)
        mMaxSelectCount = typedArray.getInt(R.styleable.SelectTextLayout_maxSelectCount, mMaxSelectCount)
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

    fun setTextViewStyle(@LayoutRes childRes: Int = 0, @DrawableRes selectRes: Int = 0, @ColorInt selectTextColor: Int = 0,
                 @DrawableRes normalRes: Int = 0, @ColorInt normalTextColor: Int = 0,
                 @DrawableRes disableRes: Int = 0, @ColorInt disableTextColor: Int = 0) {
        if (childRes != 0) mLayout = childRes
        if (selectRes != 0) selectStyle = selectRes
        if (normalRes != 0) normalStyle = normalRes
        if (disableRes != 0) disableStyle = disableRes
        if (selectTextColor != 0) this.selectTextColor = selectTextColor
        if (normalTextColor != 0) this.normalTextColor = normalTextColor
        if (disableTextColor != 0) this.disableTextColor = disableTextColor
    }

    /**
     * 重置所有子View
     */
    fun initChild(childList: ArrayList<SelectTextAttr>, enableUnSelect: Boolean = false) {
        // 不适用于子View过多的情况
        removeAllViews()
        mViewList = arrayListOf()
        for (i in 0 until childList.size) {
            val child = LayoutInflater.from(context).inflate(mLayout, this, false) as TextView
            val attr = childList[i]
            child.text = attr.name
            if (attr.enable) {      // 可用
                child.isSelected = attr.select
                if (child.isSelected) { selectChild(child) } else { unSelectChild(child) }
            } else {            //禁用态，置灰，不可选中，无点击事件
                child.setTextColor(disableTextColor)
                child.setBackgroundResource(disableStyle)
                attr.select = false
                child.isSelected = false
            }
            addView(child)
            mViewList.add(child)
            child.setOnClickListener {
                if (attr.enable) {
                    if (child.isSelected) {
                        if (enableUnSelect) {
                            unSelectChild(child)
                            onSelectChangeListener?.onUnSelect(i, child)
                        }
                    } else {
                        selectChild(child)
                        onSelectChangeListener?.onSelect(i, child)
                    }
                }
            }
        }
        // 适配数据长度后，检查修正min和max是否不符合规则
        fixSelectCount()
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

    fun unSelectChild(child: TextView) {
        child.setTextColor(normalTextColor)
        child.setBackgroundResource(normalStyle)
        if (mSelectedChildList.size > mMinSelectCount && child.isSelected) {
            child.isSelected = false
            mSelectedChildList.remove(child)
        }
    }

    fun selectChild(child: TextView) {
        child.setTextColor(selectTextColor)
        child.setBackgroundResource(selectStyle)
        child.isSelected = true
        mSelectedChildList.addLast(child)
        if (mSelectedChildList.size > mMaxSelectCount) {
            mSelectedChildList.first.run {
                isSelected = false
                unSelectChild(this)
            }
            mSelectedChildList.removeFirst()
        }
    }

    fun getSelectList() : LinkedList<TextView> {
        return mSelectedChildList
    }

    fun setSelectListener(selectListener: OnSelectChangeListener?) {
        onSelectChangeListener = selectListener
    }

    interface OnSelectChangeListener {
        fun onSelect(pos: Int, view: View)
        fun onUnSelect(pos: Int, view: View)
    }

}

class SelectTextAttr(var name: String?, var select: Boolean = false, var enable: Boolean = true)