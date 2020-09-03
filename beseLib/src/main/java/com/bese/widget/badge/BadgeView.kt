package com.bese.widget.badge

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcelable
import android.text.TextPaint
import android.text.TextUtils
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import com.blankj.utilcode.util.SizeUtils
import kotlin.math.max

/**
 * 徽标组件
 *      徽标文字可能会有少许偏移
 */
class BadgeView constructor(context: Context) : View(context) {

    companion object {
        var DEF_TET_SP: Float = 12f
        var DEF_TXT_PADDING_DP: Float = 4f
        const val DEF_OFFSET_X: Float = 0f
        const val DEF_OFFSET_Y: Float = 0f
    }

    @ColorInt private var mBadgeBgColor = Color.RED
    private var mBadgeTextColor = Color.WHITE
    private var mBgBorderColor = 0
    private var mDrawableBg: Drawable? = null
    private var mBitmapClip: Bitmap? = null
    private var mDrawableBgClip = false
    private var mBgBorderWidth: Int = 0
    private var mBadgeTextSize: Float = 0f
    private var mBadgePadding: Int = 0
    private var mBadgeNumber = 0
    /** 精确展示文本，不精确展示数字时，可以展示为99+ */
    private var mExact = false
    private var mShowShadow = false
    private var mBadgeText: String? = null
    private var mBadgeGravity = 0
    private var mGravityOffsetX: Int = 0
    private var mGravityOffsetY: Int = 0
    private var mTargetView: View? = null
    private var mWidth = 0
    private var mHeight = 0
    private var mBadgeTextRect: RectF = RectF()
    private var mBadgeTextFontMetrics: Paint.FontMetrics? = null
    private var mBadgeBgRect: RectF = RectF()
    private var mBadgeCenter: PointF = PointF()
    private var mRowBadgeCenter: PointF = PointF()
    private var mBadgeTextPaint = TextPaint()
    private var mBadgeBgPaint: Paint = Paint()
    private var mBadgeBgBorderPaint: Paint = Paint()
    private var mActivityRoot: ViewGroup? = null

    private fun init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        mBadgeTextPaint.isAntiAlias = true
        mBadgeTextPaint.isSubpixelText = true
        mBadgeTextPaint.isFakeBoldText = true
        mBadgeTextPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        mBadgeBgPaint.isAntiAlias = true
        mBadgeBgPaint.style = Paint.Style.FILL
        mBadgeBgBorderPaint.isAntiAlias = true
        mBadgeBgBorderPaint.style = Paint.Style.STROKE
        mBadgeTextSize = SizeUtils.dp2px(DEF_TET_SP).toFloat()
        mBadgePadding = SizeUtils.dp2px(DEF_TXT_PADDING_DP)
        mBadgeNumber = 0
        mBadgeGravity = Gravity.END or Gravity.TOP
        mGravityOffsetX = SizeUtils.dp2px(DEF_OFFSET_X)
        mGravityOffsetY = SizeUtils.dp2px(DEF_OFFSET_Y)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            translationZ = 1000f
        }
    }

    fun bind(sourceView: View?): BadgeView {
        checkNotNull(sourceView) { "targetView can not be null" }
        if (parent != null) {
            (parent as ViewGroup).removeView(this)
        }
        val parentView = sourceView.parent
        if (parentView != null && parentView is ViewGroup) {
            mTargetView = sourceView
            if (parentView is BadgeContainer) {
                // 可能重复绑定？
                parentView.addView(this)
            } else {
                val newFrame = parentView
                val index = newFrame.indexOfChild(sourceView)
                val targetParams = sourceView.layoutParams
                newFrame.removeView(sourceView)
                val badgeContainer = BadgeContainer(context)
                if (newFrame is RelativeLayout) {
                    badgeContainer.id = sourceView.id
                }
                newFrame.addView(badgeContainer, index, targetParams)
                badgeContainer.addView(sourceView)
                badgeContainer.addView(this)
            }
        } else {
            throw IllegalStateException("targetView must have a parent")
        }
        return this
    }

    fun getTargetView(): View? {
        return mTargetView
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mActivityRoot == null) {
            findViewRoot(mTargetView)
        }
    }

    private fun findViewRoot(view: View?) {
        mActivityRoot = view?.rootView as ViewGroup
        if (mActivityRoot == null) {
            findActivityRoot(view)
        }
    }

    private fun findActivityRoot(view: View?) {
        if (view?.parent != null && view.parent is View) {
            findActivityRoot(view.parent as View)
        } else if (view is ViewGroup) {
            mActivityRoot = view
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }

    override fun onDraw(canvas: Canvas) {
        if (mBadgeText != null) {
            initPaints()
            val badgeRadius = badgeCircleRadius
            findBadgeCenter()
            drawBadge(canvas, mBadgeCenter, badgeRadius)
        }
    }

    private fun initPaints() {
        mBadgeBgPaint.color = mBadgeBgColor
        mBadgeBgBorderPaint.color = mBgBorderColor
        mBadgeBgBorderPaint.strokeWidth = mBgBorderWidth.toFloat()
        mBadgeTextPaint.color = mBadgeTextColor
        mBadgeTextPaint.textAlign = Paint.Align.CENTER
    }

    private fun drawBadge(canvas: Canvas, center: PointF, rad: Float) {
        var radius = rad
        if (center.x == -1000f && center.y == -1000f) { return }
        if (mBadgeText?.isEmpty() == true || mBadgeText?.length == 1) {
            mBadgeBgRect.left = center.x - radius.toInt()
            mBadgeBgRect.top = center.y - radius.toInt()
            mBadgeBgRect.right = center.x + radius.toInt()
            mBadgeBgRect.bottom = center.y + radius.toInt()
            if (mDrawableBg != null) {
                drawBadgeBackground(canvas)
            } else {
                canvas.drawCircle(center.x, center.y, radius, mBadgeBgPaint)
                if (mBgBorderColor != 0 && mBgBorderWidth > 0) {
                    canvas.drawCircle(center.x, center.y, radius, mBadgeBgBorderPaint)
                }
            }
        } else {
            mBadgeBgRect.left = center.x - (mBadgeTextRect.width() / 2f + mBadgePadding)
            mBadgeBgRect.top = center.y - (mBadgeTextRect.height() / 2f + mBadgePadding * 0.5f)
            mBadgeBgRect.right = center.x + (mBadgeTextRect.width() / 2f + mBadgePadding)
            mBadgeBgRect.bottom = center.y + (mBadgeTextRect.height() / 2f + mBadgePadding * 0.5f)
            radius = mBadgeBgRect.height() / 2f
            if (mDrawableBg != null) {
                drawBadgeBackground(canvas)
            } else {
                canvas.drawRoundRect(mBadgeBgRect, radius, radius, mBadgeBgPaint)
                if (mBgBorderColor != 0 && mBgBorderWidth > 0) {
                    canvas.drawRoundRect(mBadgeBgRect, radius, radius, mBadgeBgBorderPaint)
                }
            }
        }
        if (mBadgeText?.isNotEmpty() == true) {
            val y = (mBadgeBgRect.bottom + mBadgeBgRect.top - (mBadgeTextFontMetrics?.bottom ?: 0f) - (mBadgeTextFontMetrics?.top ?: 0f)) / 2f
            canvas.drawText(mBadgeText ?: "", center.x, y, mBadgeTextPaint)
        }
    }

    private fun drawBadgeBackground(canvas: Canvas) {
        mBadgeBgPaint.setShadowLayer(0f, 0f, 0f, 0)
        val left = mBadgeBgRect.left.toInt()
        val top = mBadgeBgRect.top.toInt()
        var right = mBadgeBgRect.right.toInt()
        var bottom = mBadgeBgRect.bottom.toInt()
        if (mDrawableBgClip) {
            right = left + (mBitmapClip?.width ?: 0)
            bottom = top + (mBitmapClip?.height ?: 0)
            canvas.saveLayer(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        }
        mDrawableBg?.setBounds(left, top, right, bottom)
        mDrawableBg?.draw(canvas)
        if (mDrawableBgClip) {
            mBadgeBgPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            mBitmapClip?.run { canvas.drawBitmap(this, left.toFloat(), top.toFloat(), mBadgeBgPaint) }
            canvas.restore()
            mBadgeBgPaint.xfermode = null
            if (mBadgeText?.isEmpty() == true || mBadgeText?.length == 1) {
                canvas.drawCircle(mBadgeBgRect.centerX(), mBadgeBgRect.centerY(), mBadgeBgRect.width() / 2f, mBadgeBgBorderPaint)
            } else {
                canvas.drawRoundRect(mBadgeBgRect, mBadgeBgRect.height() / 2, mBadgeBgRect.height() / 2, mBadgeBgBorderPaint)
            }
        } else {
            canvas.drawRect(mBadgeBgRect, mBadgeBgBorderPaint)
        }
    }

    private fun createClipLayer() {
        if (mBadgeText == null) { return }
        if (!mDrawableBgClip) { return }
        if (mBitmapClip != null && mBitmapClip?.isRecycled == false) { mBitmapClip?.recycle() }

        val radius = badgeCircleRadius
        if (mBadgeText?.isEmpty() == true || mBadgeText?.length == 1) {
            mBitmapClip = Bitmap.createBitmap(radius.toInt() * 2, radius.toInt() * 2, Bitmap.Config.ARGB_8888)
            val srcCanvas = Canvas(mBitmapClip!!)
            srcCanvas.drawCircle(srcCanvas.width / 2f, srcCanvas.height / 2f, srcCanvas.width / 2f, mBadgeBgPaint)
        } else {
            mBitmapClip = Bitmap.createBitmap(
                (mBadgeTextRect.width() + mBadgePadding * 2).toInt(),
                (mBadgeTextRect.height() + mBadgePadding).toInt(), Bitmap.Config.ARGB_8888
            )
            val srcCanvas = Canvas(mBitmapClip!!)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                srcCanvas.drawRoundRect(0f, 0f, srcCanvas.width.toFloat(), srcCanvas.height.toFloat(), srcCanvas.height / 2f, srcCanvas.height / 2f, mBadgeBgPaint)
            } else {
                srcCanvas.drawRoundRect(RectF(0f, 0f, srcCanvas.width.toFloat(), srcCanvas.height.toFloat()), srcCanvas.height / 2f, srcCanvas.height / 2f, mBadgeBgPaint)
            }
        }
    }

    private val badgeCircleRadius: Float
        private get() = if (mBadgeText?.isEmpty() == true) {
            mBadgePadding.toFloat()
        } else if (mBadgeText?.length == 1) {
            if (mBadgeTextRect.height() > mBadgeTextRect.width()) mBadgeTextRect.height() / 2f + mBadgePadding * 0.5f else mBadgeTextRect.width() / 2f + mBadgePadding * 0.5f
        } else {
            mBadgeBgRect.height() / 2f
        }

    private fun findBadgeCenter() {
        val rectWidth = max(mBadgeTextRect.height(), mBadgeTextRect.width())
        when (mBadgeGravity) {
            Gravity.START or Gravity.TOP -> {
                mBadgeCenter.x = mGravityOffsetX + mBadgePadding + rectWidth / 2f
                mBadgeCenter.y = mGravityOffsetY + mBadgePadding + mBadgeTextRect.height() / 2f
            }
            Gravity.START or Gravity.BOTTOM -> {
                mBadgeCenter.x = mGravityOffsetX + mBadgePadding + rectWidth / 2f
                mBadgeCenter.y = mHeight - (mGravityOffsetY + mBadgePadding + mBadgeTextRect.height() / 2f)
            }
            Gravity.END or Gravity.TOP -> {
                mBadgeCenter.x = mWidth - (mGravityOffsetX + mBadgePadding + rectWidth / 2f)
                mBadgeCenter.y = mGravityOffsetY + mBadgePadding + mBadgeTextRect.height() / 2f
            }
            Gravity.END or Gravity.BOTTOM -> {
                mBadgeCenter.x = mWidth - (mGravityOffsetX + mBadgePadding + rectWidth / 2f)
                mBadgeCenter.y = mHeight - (mGravityOffsetY + mBadgePadding + mBadgeTextRect.height() / 2f)
            }
            Gravity.CENTER -> {
                mBadgeCenter.x = mWidth / 2f
                mBadgeCenter.y = mHeight / 2f
            }
            Gravity.CENTER or Gravity.TOP -> {
                mBadgeCenter.x = mWidth / 2f
                mBadgeCenter.y = mGravityOffsetY + mBadgePadding + mBadgeTextRect.height() / 2f
            }
            Gravity.CENTER or Gravity.BOTTOM -> {
                mBadgeCenter.x = mWidth / 2f
                mBadgeCenter.y = mHeight - (mGravityOffsetY + mBadgePadding + mBadgeTextRect.height() / 2f)
            }
            Gravity.CENTER or Gravity.START -> {
                mBadgeCenter.x = mGravityOffsetX + mBadgePadding + rectWidth / 2f
                mBadgeCenter.y = mHeight / 2f
            }
            Gravity.CENTER or Gravity.END -> {
                mBadgeCenter.x = mWidth - (mGravityOffsetX + mBadgePadding + rectWidth / 2f)
                mBadgeCenter.y = mHeight / 2f
            }
            else -> { }
        }
        initRowBadgeCenter()
    }

    private fun initRowBadgeCenter() {
        val screenPoint = IntArray(2)
        getLocationOnScreen(screenPoint)
        mRowBadgeCenter.x = mBadgeCenter.x + screenPoint[0]
        mRowBadgeCenter.y = mBadgeCenter.y + screenPoint[1]
    }

    private fun measureText() {
        mBadgeTextRect.left = 0f
        mBadgeTextRect.top = 0f
        if (TextUtils.isEmpty(mBadgeText)) {
            mBadgeTextRect.right = 0f
            mBadgeTextRect.bottom = 0f
        } else {
            mBadgeTextPaint.textSize = mBadgeTextSize
            mBadgeTextRect.right = mBadgeTextPaint.measureText(mBadgeText)
            mBadgeTextFontMetrics = mBadgeTextPaint.fontMetrics
            mBadgeTextFontMetrics?.run {
                mBadgeTextRect.bottom = descent - ascent
            }
        }
        createClipLayer()
    }

    fun hide() {
        setBadgeNumber(0)
    }

    /**
     * @param badgeNumber equal to zero badge will be hidden, less than zero show dot
     */
    fun setBadgeNumber(badgeNumber: Int): BadgeView {
        mBadgeNumber = badgeNumber
        when {
            mBadgeNumber < 0 -> {
                mBadgeText = ""
            }
            mBadgeNumber > 99 -> {
                mBadgeText = if (mExact) mBadgeNumber.toString() else "99+"
            }
            mBadgeNumber in 1..99 -> {
                mBadgeText = mBadgeNumber.toString()
            }
            mBadgeNumber == 0 -> {
                mBadgeText = null
            }
        }
        measureText()
        invalidate()
        return this
    }

    fun setBadgeText(badgeText: String?): BadgeView {
        mBadgeText = badgeText
        mBadgeNumber = 1
        measureText()
        invalidate()
        return this
    }

    fun setBadgePoint() {
        setBadgeText("")
    }

    fun setBadgeHide() {
        setBadgeText(null)
    }

    fun getBadgeText(): String? {
        return mBadgeText
    }

    fun setExactMode(isExact: Boolean): BadgeView {
        mExact = isExact
        if (mBadgeNumber > 99) {
            setBadgeNumber(mBadgeNumber)
        }
        return this
    }

    fun isExactMode(): Boolean {
        return mExact
    }

    fun setShowShadow(showShadow: Boolean): BadgeView {
        mShowShadow = showShadow
        invalidate()
        return this
    }

    fun isShowShadow(): Boolean {
        return mShowShadow
    }

    fun setBadgeBgColor(@ColorInt color: Int): BadgeView {
        mBadgeBgColor = color
        if (mBadgeBgColor == Color.TRANSPARENT) {
            mBadgeTextPaint.xfermode = null
        } else {
            mBadgeTextPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        }
        invalidate()
        return this
    }

    fun stroke(color: Int, width: Int): BadgeView {
        mBgBorderColor = color
        mBgBorderWidth = width
        invalidate()
        return this
    }

    fun getBadgeBgColor(): Int {
        return mBadgeBgColor
    }

    fun setBadgeBackground(drawable: Drawable?): BadgeView {
        return setBadgeBackground(drawable, false)
    }

    fun setBadgeBackground(drawable: Drawable?, clip: Boolean): BadgeView {
        mDrawableBgClip = clip
        mDrawableBg = drawable
        createClipLayer()
        invalidate()
        return this
    }

    fun getBadgeBackground(): Drawable? {
        return mDrawableBg
    }

    fun setBadgeTextColor(color: Int): BadgeView {
        mBadgeTextColor = color
        invalidate()
        return this
    }

    fun getBadgeTextColor(): Int {
        return mBadgeTextColor
    }

    fun setBadgeTextSize(size: Int): BadgeView {
        mBadgeTextSize = size.toFloat()
        measureText()
        invalidate()
        return this
    }

    fun getBadgeTextSize(): Float {
        return mBadgeTextSize
    }

    fun setBadgePadding(padding: Int): BadgeView {
        mBadgePadding = padding
        createClipLayer()
        invalidate()
        return this
    }

    fun getBadgePadding(): Int {
        return mBadgePadding
    }

    /**
     * @param gravity only support Gravity.START | Gravity.TOP , Gravity.END | Gravity.TOP ,
     * Gravity.START | Gravity.BOTTOM , Gravity.END | Gravity.BOTTOM ,
     * Gravity.CENTER , Gravity.CENTER | Gravity.TOP , Gravity.CENTER | Gravity.BOTTOM ,
     * Gravity.CENTER | Gravity.START , Gravity.CENTER | Gravity.END
     */
    fun setBadgeGravity(gravity: Int): BadgeView {
        if (gravity == Gravity.START or Gravity.TOP || gravity == Gravity.END or Gravity.TOP || gravity == Gravity.START or Gravity.BOTTOM || gravity == Gravity.END or Gravity.BOTTOM || gravity == Gravity.CENTER || gravity == Gravity.CENTER or Gravity.TOP || gravity == Gravity.CENTER or Gravity.BOTTOM || gravity == Gravity.CENTER or Gravity.START || gravity == Gravity.CENTER or Gravity.END
        ) {
            mBadgeGravity = gravity
            invalidate()
        } else {
            throw IllegalStateException(
                "only support Gravity.START | Gravity.TOP , Gravity.END | Gravity.TOP , " +
                        "Gravity.START | Gravity.BOTTOM , Gravity.END | Gravity.BOTTOM , Gravity.CENTER" +
                        " , Gravity.CENTER | Gravity.TOP , Gravity.CENTER | Gravity.BOTTOM ," +
                        "Gravity.CENTER | Gravity.START , Gravity.CENTER | Gravity.END"
            )
        }
        return this
    }

    fun getBadgeGravity(): Int {
        return mBadgeGravity
    }

    fun setGravityOffset(offsetX: Int, offsetY: Int): BadgeView {
        mGravityOffsetX = offsetX
        mGravityOffsetY = offsetY
        invalidate()
        return this
    }

    fun getGravityOffsetX(): Int {
        return mGravityOffsetX
    }

    fun getGravityOffsetY(): Int {
        return mGravityOffsetY
    }

    private inner class BadgeContainer(context: Context?) : ViewGroup(context) {
        override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
            if (parent !is RelativeLayout) {
                super.dispatchRestoreInstanceState(container)
            }
        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                child.layout(0, 0, child.measuredWidth, child.measuredHeight)
            }
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            var targetView: View? = null
            var badgeView: View? = null
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child !is BadgeView) {
                    targetView = child
                } else {
                    badgeView = child
                }
            }
            if (targetView == null) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            } else {
                targetView.measure(widthMeasureSpec, heightMeasureSpec)
                badgeView?.measure(
                    MeasureSpec.makeMeasureSpec(targetView.measuredWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(targetView.measuredHeight, MeasureSpec.EXACTLY)
                )
                setMeasuredDimension(targetView.measuredWidth, targetView.measuredHeight)
            }
        }
    }

    init {
        init()
    }
}