package com.bese.view

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import com.bese.R
import com.blankj.utilcode.util.SizeUtils

/**
 *
 * 文字自动轮播
 */
class TextBannerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    /**文字是否为单行,默认false */
    private var isSingleLine = true

    /**设置文字颜色,默认黑色 */
    private var mTextColor = Color.BLACK

    private var mTextSize = 14f

    /**设置文字尺寸,默认16px */
    private var mGravity = Gravity.START or Gravity.CENTER_VERTICAL

    private var mDirection = DIRECTION_BOTTOM_TO_TOP

    @AnimRes private var inAnimResId: Int = R.anim.banner_txt_right_in

    @AnimRes private var outAnimResId: Int = R.anim.banner_txt_left_out

    /**文字切换时间间隔 */
    private var mInterval = 3000
    /**文字切换动画时长 */
    private var animDuration = 500
    /** 对齐方式 */
    private var gravityType = GRAVITY_LEFT

    private var mFlags = 0

    private var mTypeface = Typeface.NORMAL

    private var mListener: TextBannerItemClickListener? = null

    private var mDataList: ArrayList<String>? = null

    private var isStarted = false
    private var isDetachedFromWindow = false

    private var mViewFlipper: ViewFlipper? = null

    /**初始化控件 */
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextBannerView, defStyleAttr, 0)
        mTextSize = typedArray.getDimension(R.styleable.TextBannerView_textBannerSize, mTextSize)
        setTextSize(SizeUtils.px2sp(mTextSize).toFloat())
        mTextColor = typedArray.getColor(R.styleable.TextBannerView_textBannerColor, mTextColor)
        setTextColor(mTextColor)
        isSingleLine = typedArray.getBoolean(R.styleable.TextBannerView_textBannerSingleLine, false)
        setSingleLine(isSingleLine)
        gravityType = typedArray.getInt(R.styleable.TextBannerView_textBannerGravity, GRAVITY_LEFT)
        setGravityType(gravityType)
        mTypeface = typedArray.getInt(R.styleable.TextBannerView_textBannerTypeface, mTypeface)
        setTypeFace(mTypeface)
        mFlags = typedArray.getInt(R.styleable.TextBannerView_textBannerFlags, mFlags)
        if (mFlags >= 0) { setTextFlag(mFlags) }
        mInterval = typedArray.getInteger(R.styleable.TextBannerView_textBannerInterval, mInterval)
        setPlayInterval(mInterval)
        animDuration = typedArray.getInt(R.styleable.TextBannerView_textBannerAnimDuration, animDuration)
        setAnimDuration(animDuration)
        mDirection = typedArray.getInt(R.styleable.TextBannerView_textBannerDirection, mDirection)
        setDirection(mDirection)
        typedArray.recycle()

        // Create a ViewFlipper
        mViewFlipper = ViewFlipper(context)
        mViewFlipper?.run {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setOnClickListener {
                mDataList?.let {
                    if (displayedChild < it.size) {
                        mListener?.onItemClick(it[displayedChild], displayedChild)
                    }
                }
            }
        }
        addView(mViewFlipper)
        startViewAnimator()
    }

    fun setTextColor(@ColorInt color: Int) {
        mTextColor = color
    }

    fun setSingleLine(singleLine: Boolean) {
        isSingleLine = singleLine
    }

    fun setTextSize(textFloat: Float) {
        mTextSize = textFloat
    }

    /**
     * 设置字体样式
     */
    fun setTypeFace(typeface: Int) {
        //字体样式
        mTypeface = when (typeface) {
            TYPE_BOLD -> Typeface.BOLD
            TYPE_ITALIC -> Typeface.ITALIC
            TYPE_ITALIC_BOLD -> Typeface.ITALIC or Typeface.BOLD
            else -> Typeface.NORMAL
        }
    }

    /**
     * 设置对齐方式
     */
    fun setGravityType(gravityType: Int) {
        mGravity = when (gravityType) {
            GRAVITY_LEFT -> Gravity.START or Gravity.CENTER_VERTICAL
            GRAVITY_CENTER -> Gravity.CENTER
            GRAVITY_RIGHT -> Gravity.END or Gravity.CENTER_VERTICAL
            else -> Gravity.START or Gravity.CENTER_VERTICAL
        }
    }

    /**
     * 设置字体样式：划线等等
     */
    fun setTextFlag(flag: Int) {
        //字体划线
        mFlags = when (flag) {
            STRIKE -> Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
            UNDER_LINE -> Paint.UNDERLINE_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
            else -> 0 or Paint.ANTI_ALIAS_FLAG
        }
    }

    /**
     * 设置动画时长
     */
    fun setAnimDuration(duration: Int) {
        this.animDuration = duration
    }

    /**
     * 设置动画间隔
     */
    fun setPlayInterval(duration: Int) {
        this.mInterval = duration
    }

    /**
     * 设置动画方向
     */
    fun setDirection(direction: Int) {
        when (direction) {
            DIRECTION_BOTTOM_TO_TOP -> {
                inAnimResId = R.anim.banner_txt_bottom_in
                outAnimResId = R.anim.banner_txt_top_out
            }
            DIRECTION_TOP_TO_BOTTOM -> {
                inAnimResId = R.anim.banner_txt_top_in
                outAnimResId = R.anim.banner_txt_bottom_out
            }
            DIRECTION_RIGHT_TO_LEFT -> {
                inAnimResId = R.anim.banner_txt_right_in
                outAnimResId = R.anim.banner_txt_left_out
            }
            DIRECTION_LEFT_TO_RIGHT -> {
                inAnimResId = R.anim.banner_txt_left_in
                outAnimResId = R.anim.banner_txt_right_out
            }
            else -> {
                inAnimResId = R.anim.banner_txt_bottom_in
                outAnimResId = R.anim.banner_txt_top_out
            }
        }
    }

    /**暂停动画 */
    fun stopViewAnimator() {
        if (isStarted) {
            removeCallbacks(mRunnable)
            isStarted = false
        }
    }

    /**开始动画 */
    fun startViewAnimator() {
        if (!isStarted) {
            if (!isDetachedFromWindow) {
                isStarted = true
                postDelayed(mRunnable, mInterval.toLong())
            }
        }
    }

    /**
     * 设置延时间隔
     */
    private val mRunnable = AnimRunnable()

    private inner class AnimRunnable : Runnable {
        override fun run() {
            if (isStarted) {
                setInAndOutAnimation(inAnimResId, outAnimResId)
                mViewFlipper?.showNext() //手动显示下一个子view。
                postDelayed(this, mInterval + animDuration.toLong())
            } else {
                stopViewAnimator()
            }
        }
    }

    /**
     * 设置进入动画和离开动画
     *
     * @param inAnimResId  进入动画的resID
     * @param outAnimResID 离开动画的resID
     */
    private fun setInAndOutAnimation(@AnimRes inAnimResId: Int, @AnimRes outAnimResID: Int) {
        val inAnim = AnimationUtils.loadAnimation(context, inAnimResId)
        inAnim.duration = animDuration.toLong()
        mViewFlipper?.inAnimation = inAnim
        val outAnim = AnimationUtils.loadAnimation(context, outAnimResID)
        outAnim.duration = animDuration.toLong()
        mViewFlipper?.outAnimation = outAnim
    }

    /**设置数据集合 */
    fun setData(data: ArrayList<String>?) {
        mDataList = data
        mViewFlipper?.removeAllViews()
        mDataList?.run {
            if (isNotEmpty()) {
                for (i in indices) {
                    val textView = TextView(context)
                    setTextView(textView, i)
                    mViewFlipper?.addView(textView, i)
                }
            }
        }
    }

    /**
     * 设置数据集合伴随drawable-icon
     * @param data 数据
     * @param drawable 图标
     * @param size 图标尺寸
     * @param direction 图标位于文字方位
     * @param childFillArea 文字是否填充宽度
     */
    fun setDataWithDrawableIcon(data: ArrayList<String>?, drawable: Drawable?, size: Int, direction: Int, childFillArea: Boolean) {
        mDataList = data
        mViewFlipper?.removeAllViews()
        mDataList?.run {
            if (isNotEmpty()) {
                for (i in indices) {
                    val textView = TextView(context)
                    setTextView(textView, i)
                    textView.compoundDrawablePadding = 10
                    val scale = resources.displayMetrics.density
                    val muchDp = (size * scale + 0.5f).toInt()
                    drawable?.run {
                        setBounds(0, 0, muchDp, muchDp)
                        when (direction) {
                            Gravity.START -> textView.setCompoundDrawables(this, null, null, null)
                            Gravity.TOP -> textView.setCompoundDrawables(null, this, null, null)
                            Gravity.END -> textView.setCompoundDrawables(null, null, this, null)
                            Gravity.BOTTOM -> textView.setCompoundDrawables(null, null, null, this)
                        }
                    }
                    val linearLayout = LinearLayout(context)
                    linearLayout.orientation = LinearLayout.HORIZONTAL
                    linearLayout.gravity = mGravity
                    val param = LinearLayout.LayoutParams(if (childFillArea) LinearLayout.LayoutParams.MATCH_PARENT else LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    linearLayout.addView(textView, param)
                    mViewFlipper?.addView(linearLayout, i)          //添加子view,并标识子view位置
                }
            }
        }

    }

    /**设置TextView */
    private fun setTextView(textView: TextView, position: Int) {
        mDataList?.let {
            if (position < it.size) {
                textView.run {
                    text = it[position]
                    setTextColor(mTextColor)
                    textSize = mTextSize
                    setTypeface(null, mTypeface)
                    isSingleLine = true
                    paint.flags = mFlags
                    gravity = mGravity
                    ellipsize = TextUtils.TruncateAt.END
                }
            }
        }

        textView.text = mDataList?.get(position)
        textView.ellipsize = TextUtils.TruncateAt.END
    }

    /**设置点击监听事件回调 */
    fun setItemOnClickListener(listener: TextBannerItemClickListener) {
        mListener = listener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isDetachedFromWindow = true
        stopViewAnimator()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isDetachedFromWindow = false
        startViewAnimator()
    }

    companion object {

        /**文字显示位置,默认左边居中 */
        private const val GRAVITY_LEFT = 0
        private const val GRAVITY_CENTER = 1
        private const val GRAVITY_RIGHT = 2
        private const val DIRECTION_BOTTOM_TO_TOP = 0
        private const val DIRECTION_TOP_TO_BOTTOM = 1
        private const val DIRECTION_RIGHT_TO_LEFT = 2
        private const val DIRECTION_LEFT_TO_RIGHT = 3

        /**文字划线 */
        private const val UNDER_LINE = 1
        private const val STRIKE = 2

        /**设置字体类型：加粗、斜体、斜体加粗 */
        private const val TYPE_NORMAL = 0
        private const val TYPE_BOLD = 1
        private const val TYPE_ITALIC = 2
        private const val TYPE_ITALIC_BOLD = 3
    }

    init {
        init(context, attrs, defStyleAttr)
    }
}

interface TextBannerItemClickListener {
    fun onItemClick(data: String?, position: Int)
}