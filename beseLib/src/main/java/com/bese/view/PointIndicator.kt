package com.bese.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.bese.R
import com.blankj.utilcode.util.ReflectUtils
import com.youth.banner.Banner

/**
 * Banner point Indicator
 */
class PointIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    @DrawableRes
    private var childResUnSelected: Int = R.drawable.shape_indicator_unselected

    @DrawableRes
    private var childResSelected: Int = R.drawable.shape_indicator_selected

    @Dimension
    private var childMargin = getDp(2f)

    @Dimension
    private var childSize = getDp(4f)

    @Dimension
    private var childWidthSelected = getDp(12f)

    @ColorInt
    private var childColorUnSelected = Color.parseColor("#7FFFFFFF")

    @ColorInt
    private var childColorSelected = Color.WHITE

    /**
     * 子View数量
     */
    private var childNum = 0

    private fun getDp(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
//        自定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PointIndicator, defStyleAttr, defStyleRes)
        for (i in 0 until typedArray.indexCount) {
            val attr = typedArray.getIndex(i)
            if (attr == R.styleable.PointIndicator_child_shape_unselected) {
                childResUnSelected = typedArray.getResourceId(R.styleable.PointIndicator_child_shape_unselected, childResUnSelected)
            } else if (attr == R.styleable.PointIndicator_child_shape_selected) {
                childResSelected = typedArray.getResourceId(R.styleable.PointIndicator_child_shape_selected, childResSelected)
            } else if (attr == R.styleable.PointIndicator_child_size) {
                childSize = typedArray.getDimensionPixelSize(R.styleable.PointIndicator_child_size, childSize)
            } else if (attr == R.styleable.PointIndicator_child_width_selected) {
                childWidthSelected = typedArray.getDimensionPixelSize(R.styleable.PointIndicator_child_width_selected, childWidthSelected)
            } else if (attr == R.styleable.PointIndicator_child_margin) {
                childMargin = typedArray.getDimensionPixelSize(R.styleable.PointIndicator_child_margin, childMargin)
            } else if (attr == R.styleable.PointIndicator_child_color_unselected) {
                childColorUnSelected = typedArray.getColor(R.styleable.PointIndicator_child_color_unselected, Color.parseColor("#66000000"))
            } else if (attr == R.styleable.PointIndicator_child_color_selected) {
                childColorSelected = typedArray.getColor(R.styleable.PointIndicator_child_color_selected, Color.WHITE)
            }
        }
        typedArray.recycle()
    }

    private fun initChildren() {
        removeAllViews()
        for (i in 0 until childNum) {
            val view = View(context)
            addView(view, childSize, childSize)
            view.setBackgroundResource(childResUnSelected)
            view.backgroundTintList = ColorStateList.valueOf(childColorUnSelected)
            (view.layoutParams as LayoutParams).setMargins(childMargin, 0, childMargin, 0)
        }
    }

    private fun check(position: Int) {
        for (i in 0 until childCount) {
            if (i == position) {
                val child = getChildAt(i)
                val layoutParams = child.layoutParams as LayoutParams
                layoutParams.width = childWidthSelected.toInt()
                layoutParams.height = childSize.toInt()
                child.layoutParams = layoutParams
                child.setBackgroundResource(childResSelected)
                child.backgroundTintList = ColorStateList.valueOf(childColorSelected)
            } else {
                val child = getChildAt(i)
                val layoutParams = child.layoutParams as LayoutParams
                layoutParams.width = childSize
                layoutParams.height = childSize
                child.layoutParams = layoutParams
                child.setBackgroundResource(childResUnSelected)
                child.backgroundTintList = ColorStateList.valueOf(childColorUnSelected)
            }
        }
    }

    fun bindBanner(banner: Banner) {
        val data = ReflectUtils.reflect(banner).field("imageUrls").get<List<*>>()
        if (data != null) {
            childNum = data.size
            if (childNum > 1) {
                initChildren()
            } else {
                removeAllViews()
            }
        }
        banner.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                check(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    fun bindViewPager(viewPager: ViewPager) {
        if (viewPager.adapter != null) {
            childNum = viewPager.adapter!!.count
        }
        if (childNum > 1) {
            initChildren()
            check(0)
        }
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) { check(position) }
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    init {
        init(context, attrs, defStyleAttr, defStyleRes)
    }
}