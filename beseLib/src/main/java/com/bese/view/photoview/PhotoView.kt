package com.bese.view.photoview

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.OverScroller
import android.widget.Scroller
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * < 图片查看自定义控件 >
 */
class PhotoView : AppCompatImageView {

    private var mMinRotate = 0
    /** 动画的持续时间 */
    var animDuring = 0
    /** 最大可以缩放的倍数 */
    var maxScale = 0f
    private val mBaseMatrix = Matrix()
    private val mAnimMatrix = Matrix()
    private val mSynthesisMatrix = Matrix()
    private val mTmpMatrix = Matrix()
    private var mRotateDetector: RotateGestureDetector? = null
    private var mDetector: GestureDetector? = null
    private var mScaleDetector: ScaleGestureDetector? = null
    private var mClickListener: OnClickListener? = null
    private var mScaleType: ScaleType? = null
    private var hasMultiTouch = false
    private var hasDrawable = false
    private var isKnowSize = false
    private var hasOverTranslate = false
    var isEnableZoom = false
    var isRotateEnable = false
    private var isInit = false
    private var mAdjustViewBounds = false
    /**
     * 当前是否处于放大状态
     */
    private var isZoomUp = false
    /**
     * 是否支持旋转图片
     */
    private var canRotate = false
    private var imgLargeWidth = false
    private var imgLargeHeight = false
    private var mRotateFlag = 0f
    private var mDegrees = 0f
    private var mScale = 1.0f
    private var mTranslateX = 0
    private var mTranslateY = 0
    private var mHalfBaseRectWidth = 0f
    private var mHalfBaseRectHeight = 0f
    private val mWidgetRect = RectF()
    private val mBaseRect = RectF()
    private val mImgRect = RectF()
    private val mTmpRect = RectF()
    private val mCommonRect = RectF()
    private val mScreenCenter = PointF()
    private val mScaleCenter = PointF()
    private val mRotateCenter = PointF()
    private val mTranslate = Transform()
    private var mClip: RectF? = null
    private var mFromInfo: Info? = null
    private var mInfoTime: Long = 0
    private var mCompleteCallBack: Runnable? = null
    private var mLongClick: OnLongClickListener? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        super.setScaleType(ScaleType.MATRIX)
        if (mScaleType == null) {
            mScaleType = ScaleType.CENTER_INSIDE
        }
        mRotateDetector = RotateGestureDetector(mRotateListener)
        mDetector = GestureDetector(context, mGestureListener)
        mScaleDetector = ScaleGestureDetector(context, mScaleListener)
        val density = resources.displayMetrics.density
        MAX_OVER_SCROLL = (density * 30).toInt()
        MAX_FLING_OVER_SCROLL = (density * 30).toInt()
        MAX_OVER_RESISTANCE = (density * 140).toInt()
        mMinRotate = MIN_ROTATE
        animDuring = defaultAnimDuring
        maxScale = MAX_SCALE
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        super.setOnClickListener(listener)
        mClickListener = listener
    }

    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType == ScaleType.MATRIX) { return }
        if (scaleType != mScaleType) {
            mScaleType = scaleType
            if (isInit) { initBase() }
        }
    }

    override fun setOnLongClickListener(listener: OnLongClickListener?) {
        mLongClick = listener
    }

    /**
     * 设置动画的插入器
     */
    fun setInterpolator(interpolator: Interpolator?) {
        mTranslate.setInterpolator(interpolator)
    }

    fun setMaxAnimFromWaiteTime(wait: Int) {
        MAX_ANIM_FROM_WAITE = wait
    }

    override fun setImageResource(resId: Int) {
        var drawable: Drawable? = null
        try {
            drawable = resources.getDrawable(resId)
        } catch (e: Exception) {
        }
        setImageDrawable(drawable)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (drawable == null) {
            hasDrawable = false
            return
        }
        if (!hasSize(drawable)) {
            return
        }
        if (!hasDrawable) {
            hasDrawable = true
        }
        initBase()
    }

    private fun hasSize(d: Drawable): Boolean {
        return !((d.intrinsicHeight <= 0 || d.intrinsicWidth <= 0)
                && (d.minimumWidth <= 0 || d.minimumHeight <= 0)
                && (d.bounds.width() <= 0 || d.bounds.height() <= 0))
    }

    private fun initBase() {
        if (!hasDrawable) {
            return
        }
        if (!isKnowSize) {
            return
        }
        mBaseMatrix.reset()
        mAnimMatrix.reset()
        isZoomUp = false
        val img = drawable
        val w = width
        val h = height
        val imgW = getDrawableWidth(img)
        val imgH = getDrawableHeight(img)
        mBaseRect[0f, 0f, imgW.toFloat()] = imgH.toFloat()
        // 以图片中心点居中位移
        val tx = (w - imgW) / 2
        val ty = (h - imgH) / 2
        var sx = 1f
        var sy = 1f
        // 缩放，默认不超过屏幕大小
        if (imgW > w) {
            sx = w.toFloat() / imgW
        }
        if (imgH > h) {
            sy = h.toFloat() / imgH
        }
        val scale = if (sx < sy) sx else sy
        mBaseMatrix.reset()
        mBaseMatrix.postTranslate(tx.toFloat(), ty.toFloat())
        mBaseMatrix.postScale(scale, scale, mScreenCenter.x, mScreenCenter.y)
        mBaseMatrix.mapRect(mBaseRect)
        mHalfBaseRectWidth = mBaseRect.width() / 2
        mHalfBaseRectHeight = mBaseRect.height() / 2
        mScaleCenter.set(mScreenCenter)
        mRotateCenter.set(mScaleCenter)
        executeTranslate()
        when (mScaleType) {
            ScaleType.CENTER -> initCenter()
            ScaleType.CENTER_CROP -> initCenterCrop()
            ScaleType.CENTER_INSIDE -> initCenterInside()
            ScaleType.FIT_CENTER -> initFitCenter()
            ScaleType.FIT_START -> initFitStart()
            ScaleType.FIT_END -> initFitEnd()
            ScaleType.FIT_XY -> initFitXY()
            else -> { }
        }
        isInit = true
        mFromInfo?.run {
            if (System.currentTimeMillis() - mInfoTime < MAX_ANIM_FROM_WAITE) { animFrom(this) }
        }
        mFromInfo = null
    }

    private fun initCenter() {
        if (!hasDrawable) { return }
        if (!isKnowSize) { return }
        val img = drawable
        val imgW = getDrawableWidth(img)
        val imgH = getDrawableHeight(img)
        if (imgW > mWidgetRect.width() || imgH > mWidgetRect.height()) {
            val scaleX = imgW / mImgRect.width()
            val scaleY = imgH / mImgRect.height()
            mScale = if (scaleX > scaleY) scaleX else scaleY
            mAnimMatrix.postScale(mScale, mScale, mScreenCenter.x, mScreenCenter.y)
            executeTranslate()
            resetBase()
        }
    }

    private fun initCenterCrop() {
        if (mImgRect.width() < mWidgetRect.width() || mImgRect.height() < mWidgetRect.height()) {
            val scaleX = mWidgetRect.width() / mImgRect.width()
            val scaleY = mWidgetRect.height() / mImgRect.height()
            mScale = if (scaleX > scaleY) scaleX else scaleY
            mAnimMatrix.postScale(mScale, mScale, mScreenCenter.x, mScreenCenter.y)
            executeTranslate()
            resetBase()
        }
    }

    private fun initCenterInside() {
        if (mImgRect.width() > mWidgetRect.width() || mImgRect.height() > mWidgetRect.height()) {
            val scaleX = mWidgetRect.width() / mImgRect.width()
            val scaleY = mWidgetRect.height() / mImgRect.height()
            mScale = if (scaleX < scaleY) scaleX else scaleY
            mAnimMatrix.postScale(mScale, mScale, mScreenCenter.x, mScreenCenter.y)
            executeTranslate()
            resetBase()
        }
    }

    private fun initFitCenter() {
        if (mImgRect.width() < mWidgetRect.width()) {
            mScale = mWidgetRect.width() / mImgRect.width()
            mAnimMatrix.postScale(mScale, mScale, mScreenCenter.x, mScreenCenter.y)
            executeTranslate()
            resetBase()
        }
    }

    private fun initFitStart() {
        initFitCenter()
        val ty = -mImgRect.top
        mAnimMatrix.postTranslate(0f, ty)
        executeTranslate()
        resetBase()
        mTranslateY += ty.toInt()
    }

    private fun initFitEnd() {
        initFitCenter()
        val ty = mWidgetRect.bottom - mImgRect.bottom
        mTranslateY += ty.toInt()
        mAnimMatrix.postTranslate(0f, ty)
        executeTranslate()
        resetBase()
    }

    private fun initFitXY() {
        val scaleX = mWidgetRect.width() / mImgRect.width()
        val scaleY = mWidgetRect.height() / mImgRect.height()
        mAnimMatrix.postScale(scaleX, scaleY, mScreenCenter.x, mScreenCenter.y)
        executeTranslate()
        resetBase()
    }

    private fun resetBase() {
        val img = drawable
        val imgW = getDrawableWidth(img)
        val imgH = getDrawableHeight(img)
        mBaseRect[0f, 0f, imgW.toFloat()] = imgH.toFloat()
        mBaseMatrix.set(mSynthesisMatrix)
        mBaseMatrix.mapRect(mBaseRect)
        mHalfBaseRectWidth = mBaseRect.width() / 2
        mHalfBaseRectHeight = mBaseRect.height() / 2
        mScale = 1f
        mTranslateX = 0
        mTranslateY = 0
        mAnimMatrix.reset()
    }

    private fun executeTranslate() {
        mSynthesisMatrix.set(mBaseMatrix)
        mSynthesisMatrix.postConcat(mAnimMatrix)
        imageMatrix = mSynthesisMatrix
        mAnimMatrix.mapRect(mImgRect, mBaseRect)
        imgLargeWidth = mImgRect.width() > mWidgetRect.width()
        imgLargeHeight = mImgRect.height() > mWidgetRect.height()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!hasDrawable) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        val d = drawable
        val drawableW = getDrawableWidth(d)
        val drawableH = getDrawableHeight(d)
        val pWidth = MeasureSpec.getSize(widthMeasureSpec)
        val pHeight = MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var width = 0
        var height = 0
        var p = layoutParams
        if (p == null) {
            p = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        width = if (p.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            if (widthMode == MeasureSpec.UNSPECIFIED) { drawableW } else { pWidth }
        } else {
            if (widthMode == MeasureSpec.EXACTLY) {
                pWidth
            } else if (widthMode == MeasureSpec.AT_MOST) {
                if (drawableW > pWidth) pWidth else drawableW
            } else {
                drawableW
            }
        }
        height = if (p.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            if (heightMode == MeasureSpec.UNSPECIFIED) { drawableH } else { pHeight }
        } else {
            if (heightMode == MeasureSpec.EXACTLY) {
                pHeight
            } else if (heightMode == MeasureSpec.AT_MOST) {
                if (drawableH > pHeight) pHeight else drawableH
            } else {
                drawableH
            }
        }
        if (mAdjustViewBounds && drawableW.toFloat() / drawableH != width.toFloat() / height) {
            val hScale = height.toFloat() / drawableH
            val wScale = width.toFloat() / drawableW
            val scale = if (hScale < wScale) hScale else wScale
            width = if (p.width == ViewGroup.LayoutParams.MATCH_PARENT) width else (drawableW * scale).toInt()
            height = if (p.height == ViewGroup.LayoutParams.MATCH_PARENT) height else (drawableH * scale).toInt()
        }
        setMeasuredDimension(width, height)
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        super.setAdjustViewBounds(adjustViewBounds)
        mAdjustViewBounds = adjustViewBounds
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidgetRect[0f, 0f, w.toFloat()] = h.toFloat()
        mScreenCenter[w / 2.toFloat()] = h / 2.toFloat()
        if (!isKnowSize) {
            isKnowSize = true
            initBase()
        }
    }

    override fun draw(canvas: Canvas) {
        mClip?.run {
            canvas.clipRect(this)
            mClip = null
        }
        super.draw(canvas)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return if (isEnableZoom) {
            val action = event.actionMasked
            if (event.pointerCount >= 2) {
                hasMultiTouch = true
            }
            mDetector?.onTouchEvent(event)
            if (isRotateEnable) {
                mRotateDetector?.onTouchEvent(event)
            }
            mScaleDetector?.onTouchEvent(event)
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                onUp()
            }
            true
        } else {
            super.dispatchTouchEvent(event)
        }
    }

    private fun onUp() {
        if (mTranslate.isRunning) { return }
        if (canRotate || mDegrees % 90 != 0f) {
            var toDegrees = (mDegrees / 90).toInt() * 90.toFloat()
            val remainder = mDegrees % 90
            if (remainder > 45) {
                toDegrees += 90f
            } else if (remainder < -45) {
                toDegrees -= 90f
            }
            mTranslate.withRotate(mDegrees.toInt(), toDegrees.toInt())
            mDegrees = toDegrees
        }
        var scale = mScale
        if (mScale < 1) {
            scale = 1f
            mTranslate.withScale(mScale, 1f)
        } else if (mScale > maxScale) {
            scale = maxScale
            mTranslate.withScale(mScale, maxScale)
        }
        val cx = mImgRect.left + mImgRect.width() / 2
        val cy = mImgRect.top + mImgRect.height() / 2
        mScaleCenter[cx] = cy
        mRotateCenter[cx] = cy
        mTranslateX = 0
        mTranslateY = 0
        mTmpMatrix.reset()
        mTmpMatrix.postTranslate(-mBaseRect.left, -mBaseRect.top)
        mTmpMatrix.postTranslate(cx - mHalfBaseRectWidth, cy - mHalfBaseRectHeight)
        mTmpMatrix.postScale(scale, scale, cx, cy)
        mTmpMatrix.postRotate(mDegrees, cx, cy)
        mTmpMatrix.mapRect(mTmpRect, mBaseRect)
        doTranslateReset(mTmpRect)
        mTranslate.start()
    }

    private fun doTranslateReset(imgRect: RectF) {
        var tx = 0
        var ty = 0
        if (imgRect.width() <= mWidgetRect.width()) {
            if (!isImageCenterWidth(imgRect)) {
                tx = (-((mWidgetRect.width() - imgRect.width()) / 2 - imgRect.left)).toInt()
            }
        } else {
            if (imgRect.left > mWidgetRect.left) {
                tx = (imgRect.left - mWidgetRect.left).toInt()
            } else if (imgRect.right < mWidgetRect.right) {
                tx = (imgRect.right - mWidgetRect.right).toInt()
            }
        }
        if (imgRect.height() <= mWidgetRect.height()) {
            if (!isImageCenterHeight(imgRect)) {
                ty = (-((mWidgetRect.height() - imgRect.height()) / 2 - imgRect.top)).toInt()
            }
        } else {
            if (imgRect.top > mWidgetRect.top) {
                ty = (imgRect.top - mWidgetRect.top).toInt()
            } else if (imgRect.bottom < mWidgetRect.bottom) {
                ty = (imgRect.bottom - mWidgetRect.bottom).toInt()
            }
        }
        if (tx != 0 || ty != 0) {
            if (!mTranslate.mFlingScroller.isFinished) {
                mTranslate.mFlingScroller.abortAnimation()
            }
            mTranslate.withTranslate(mTranslateX, mTranslateY, -tx, -ty)
        }
    }

    private fun isImageCenterHeight(rect: RectF): Boolean {
        return abs(rect.top.roundToInt() - (mWidgetRect.height() - rect.height()) / 2) < 1
    }

    private fun isImageCenterWidth(rect: RectF): Boolean {
        return abs(rect.left.roundToInt() - (mWidgetRect.width() - rect.width()) / 2) < 1
    }

    private val mRotateListener: OnRotateListener = object : OnRotateListener {
        override fun onRotate(degrees: Float, focusX: Float, focusY: Float) {
            mRotateFlag += degrees
            if (canRotate) {
                mDegrees += degrees
                mAnimMatrix.postRotate(degrees, focusX, focusY)
            } else {
                if (abs(mRotateFlag) >= mMinRotate) {
                    canRotate = true
                    mRotateFlag = 0f
                }
            }
        }
    }
    private val mScaleListener: OnScaleGestureListener = object : OnScaleGestureListener {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            if (java.lang.Float.isNaN(scaleFactor) || java.lang.Float.isInfinite(scaleFactor)) {
                return false
            }
            mScale *= scaleFactor
            mAnimMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
            executeTranslate()
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {}
    }

    private fun resistanceScrollByX(overScroll: Float, detalX: Float): Float {
        return detalX * (abs(abs(overScroll) - MAX_OVER_RESISTANCE) / MAX_OVER_RESISTANCE.toFloat())
    }

    private fun resistanceScrollByY(overScroll: Float, detalY: Float): Float {
        return detalY * (abs(abs(overScroll) - MAX_OVER_RESISTANCE) / MAX_OVER_RESISTANCE.toFloat())
    }

    /**
     * 匹配两个Rect的共同部分输出到out，若无共同部分则输出0，0，0，0
     */
    private fun mapRect(r1: RectF, r2: RectF, out: RectF) {
        val l = if (r1.left > r2.left) r1.left else r2.left
        val r = if (r1.right < r2.right) r1.right else r2.right
        if (l > r) {
            out[0f, 0f, 0f] = 0f
            return
        }
        val t = if (r1.top > r2.top) r1.top else r2.top
        val b = if (r1.bottom < r2.bottom) r1.bottom else r2.bottom
        if (t > b) {
            out[0f, 0f, 0f] = 0f
            return
        }
        out[l, t, r] = b
    }

    private fun checkRect() {
        if (!hasOverTranslate) {
            mapRect(mWidgetRect, mImgRect, mCommonRect)
        }
    }

    private val mClickRunnable = Runnable {
        mClickListener?.onClick(this@PhotoView)
    }
    private val mGestureListener: GestureDetector.OnGestureListener =
        object : SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                mLongClick?.onLongClick(this@PhotoView)
            }

            override fun onDown(e: MotionEvent): Boolean {
                hasOverTranslate = false
                hasMultiTouch = false
                canRotate = false
                removeCallbacks(mClickRunnable)
                return false
            }

            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (hasMultiTouch) { return false }
                if (!imgLargeWidth && !imgLargeHeight) { return false }
                if (mTranslate.isRunning) { return false }
                var vx = velocityX
                var vy = velocityY
                if (mImgRect.left.roundToInt() >= mWidgetRect.left || mImgRect.right.roundToInt() <= mWidgetRect.right) { vx = 0f }
                if (mImgRect.top.roundToInt() >= mWidgetRect.top || mImgRect.bottom.roundToInt() <= mWidgetRect.bottom) { vy = 0f }
                if (canRotate || mDegrees % 90 != 0f) {
                    var toDegrees = (mDegrees / 90).toInt() * 90.toFloat()
                    val remainder = mDegrees % 90
                    if (remainder > 45) { toDegrees += 90f } else if (remainder < -45) { toDegrees -= 90f }
                    mTranslate.withRotate(mDegrees.toInt(), toDegrees.toInt())
                    mDegrees = toDegrees
                }
                doTranslateReset(mImgRect)
                mTranslate.withFling(vx, vy)
                mTranslate.start()
                return super.onFling(e1, e2, velocityX, velocityY)
            }

            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                var dx = distanceX
                var dy = distanceY
                if (mTranslate.isRunning) {
                    mTranslate.stop()
                }
                if (canScrollHorizontallySelf(dx)) {
                    if (dx < 0 && mImgRect.left - dx > mWidgetRect.left) {
                        dx = mImgRect.left
                    }
                    if (dx > 0 && mImgRect.right - dx < mWidgetRect.right) {
                        dx = mImgRect.right - mWidgetRect.right
                    }
                    mAnimMatrix.postTranslate(-dx, 0f)
                    mTranslateX -= dx.toInt()
                } else if (imgLargeWidth || hasMultiTouch || hasOverTranslate) {
                    checkRect()
                    if (!hasMultiTouch) {
                        if (dx < 0 && mImgRect.left - dx > mCommonRect.left) {
                            dx = resistanceScrollByX(mImgRect.left - mCommonRect.left, dx)
                        }
                        if (dx > 0 && mImgRect.right - dx < mCommonRect.right) {
                            dx = resistanceScrollByX(mImgRect.right - mCommonRect.right, dx)
                        }
                    }
                    mTranslateX -= dx.toInt()
                    mAnimMatrix.postTranslate(-dx, 0f)
                    hasOverTranslate = true
                }
                if (canScrollVerticallySelf(dy)) {
                    if (dy < 0 && mImgRect.top - dy > mWidgetRect.top) {
                        dy = mImgRect.top
                    }
                    if (dy > 0 && mImgRect.bottom - dy < mWidgetRect.bottom) {
                        dy = mImgRect.bottom - mWidgetRect.bottom
                    }
                    mAnimMatrix.postTranslate(0f, -dy)
                    mTranslateY -= dy.toInt()
                } else if (imgLargeHeight || hasOverTranslate || hasMultiTouch) {
                    checkRect()
                    if (!hasMultiTouch) {
                        if (dy < 0 && mImgRect.top - dy > mCommonRect.top) {
                            dy = resistanceScrollByY(mImgRect.top - mCommonRect.top, dy)
                        }
                        if (dy > 0 && mImgRect.bottom - dy < mCommonRect.bottom) {
                            dy = resistanceScrollByY(mImgRect.bottom - mCommonRect.bottom, dy)
                        }
                    }
                    mAnimMatrix.postTranslate(0f, -dy)
                    mTranslateY -= dy.toInt()
                    hasOverTranslate = true
                }
                executeTranslate()
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                postDelayed(mClickRunnable, 250)
                return false
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                mTranslate.stop()
                var from = 1f
                var to = 1f
                val imgcx = mImgRect.left + mImgRect.width() / 2
                val imgcy = mImgRect.top + mImgRect.height() / 2
                mScaleCenter[imgcx] = imgcy
                mRotateCenter[imgcx] = imgcy
                mTranslateX = 0
                mTranslateY = 0
                if (isZoomUp) {
                    from = mScale
                    to = 1f
                } else {
                    from = mScale
                    to = maxScale
                    mScaleCenter[e.x] = e.y
                }
                mTmpMatrix.reset()
                mTmpMatrix.postTranslate(-mBaseRect.left, -mBaseRect.top)
                mTmpMatrix.postTranslate(mRotateCenter.x, mRotateCenter.y)
                mTmpMatrix.postTranslate(-mHalfBaseRectWidth, -mHalfBaseRectHeight)
                mTmpMatrix.postRotate(mDegrees, mRotateCenter.x, mRotateCenter.y)
                mTmpMatrix.postScale(to, to, mScaleCenter.x, mScaleCenter.y)
                mTmpMatrix.postTranslate(mTranslateX.toFloat(), mTranslateY.toFloat())
                mTmpMatrix.mapRect(mTmpRect, mBaseRect)
                doTranslateReset(mTmpRect)
                isZoomUp = !isZoomUp
                mTranslate.withScale(from, to)
                mTranslate.start()
                return false
            }
        }

    fun canScrollHorizontallySelf(direction: Float): Boolean {
        if (mImgRect.width() <= mWidgetRect.width()) {
            return false
        }
        return if (direction < 0 && mImgRect.left.roundToInt() - direction >= mWidgetRect.left) {
            false
        } else direction <= 0 || mImgRect.right.roundToInt() - direction > mWidgetRect.right
    }

    fun canScrollVerticallySelf(direction: Float): Boolean {
        if (mImgRect.height() <= mWidgetRect.height()) {
            return false
        }
        return if (direction < 0 && mImgRect.top.roundToInt() - direction >= mWidgetRect.top) {
            false
        } else direction <= 0 || mImgRect.bottom.roundToInt() - direction > mWidgetRect.bottom
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return if (hasMultiTouch) {
            true
        } else canScrollHorizontallySelf(direction.toFloat())
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return if (hasMultiTouch) {
            true
        } else canScrollVerticallySelf(direction.toFloat())
    }

    private inner class InterpolatorProxy : Interpolator {
        private var mTarget: Interpolator? = DecelerateInterpolator()
        fun setTargetInterpolator(interpolator: Interpolator?) {
            mTarget = interpolator
        }

        override fun getInterpolation(input: Float): Float {
            mTarget?.run {
                return getInterpolation(input)
            }
            return input
        }
    }

    private inner class Transform internal constructor() : Runnable {
        var isRunning = false
        var mTranslateScroller: OverScroller
        var mFlingScroller: OverScroller
        var mScaleScroller: Scroller
        var mClipScroller: Scroller
        var mRotateScroller: Scroller
        var mClipCalc: ClipCalculate? = null
        var mLastFlingX = 0
        var mLastFlingY = 0
        var mLastTranslateX = 0
        var mLastTranslateY = 0
        var mClipRect = RectF()
        var mInterpolatorProxy = InterpolatorProxy()
        fun setInterpolator(interpolator: Interpolator?) {
            mInterpolatorProxy.setTargetInterpolator(interpolator)
        }

        fun withTranslate(startX: Int, startY: Int, deltaX: Int, deltaY: Int) {
            mLastTranslateX = 0
            mLastTranslateY = 0
            mTranslateScroller.startScroll(0, 0, deltaX, deltaY, animDuring)
        }

        fun withScale(form: Float, to: Float) {
            mScaleScroller.startScroll((form * 10000).toInt(), 0, ((to - form) * 10000).toInt(), 0, animDuring)
        }

        fun withClip(fromX: Float, fromY: Float, deltaX: Float, deltaY: Float, d: Int, clipCalc: ClipCalculate?) {
            mClipScroller.startScroll((fromX * 10000).toInt(), (fromY * 10000).toInt(), (deltaX * 10000).toInt(), (deltaY * 10000).toInt(), d)
            mClipCalc = clipCalc
        }

        fun withRotate(fromDegrees: Int, toDegrees: Int) {
            mRotateScroller.startScroll(fromDegrees, 0, toDegrees - fromDegrees, 0, animDuring)
        }

        fun withRotate(fromDegrees: Int, toDegrees: Int, during: Int) {
            mRotateScroller.startScroll(fromDegrees, 0, toDegrees - fromDegrees, 0, during)
        }

        fun withFling(velocityX: Float, velocityY: Float) {
            mLastFlingX = if (velocityX < 0) Int.MAX_VALUE else 0
            var distanceX = (if (velocityX > 0) abs(mImgRect.left) else mImgRect.right - mWidgetRect.right).toInt()
            distanceX = if (velocityX < 0) Int.MAX_VALUE - distanceX else distanceX
            var minX = if (velocityX < 0) distanceX else 0
            var maxX = if (velocityX < 0) Int.MAX_VALUE else distanceX
            val overX = if (velocityX < 0) Int.MAX_VALUE - minX else distanceX
            mLastFlingY = if (velocityY < 0) Int.MAX_VALUE else 0

            var distanceY = (if (velocityY > 0) abs(mImgRect.top) else mImgRect.bottom - mWidgetRect.bottom).toInt()
            distanceY = if (velocityY < 0) Int.MAX_VALUE - distanceY else distanceY
            var minY = if (velocityY < 0) distanceY else 0
            var maxY = if (velocityY < 0) Int.MAX_VALUE else distanceY
            val overY = if (velocityY < 0) Int.MAX_VALUE - minY else distanceY
            if (velocityX == 0f) {
                maxX = 0
                minX = 0
            }
            if (velocityY == 0f) {
                maxY = 0
                minY = 0
            }
            val calcOverX = if (abs(overX) < MAX_FLING_OVER_SCROLL * 2) 0 else MAX_FLING_OVER_SCROLL
            val calcOverY = if (abs(overY) < MAX_FLING_OVER_SCROLL * 2) 0 else MAX_FLING_OVER_SCROLL

            mFlingScroller.fling(mLastFlingX, mLastFlingY, velocityX.toInt(), velocityY.toInt(), minX, maxX, minY, maxY, calcOverX, calcOverY)
        }

        fun start() {
            isRunning = true
            postExecute()
        }

        fun stop() {
            removeCallbacks(this)
            mTranslateScroller.abortAnimation()
            mScaleScroller.abortAnimation()
            mFlingScroller.abortAnimation()
            mRotateScroller.abortAnimation()
            isRunning = false
        }

        override fun run() {
            var endAnim = true
            if (mScaleScroller.computeScrollOffset()) {
                mScale = mScaleScroller.currX / 10000f
                endAnim = false
            }
            if (mTranslateScroller.computeScrollOffset()) {
                val tx = mTranslateScroller.currX - mLastTranslateX
                val ty = mTranslateScroller.currY - mLastTranslateY
                mTranslateX += tx
                mTranslateY += ty
                mLastTranslateX = mTranslateScroller.currX
                mLastTranslateY = mTranslateScroller.currY
                endAnim = false
            }
            if (mFlingScroller.computeScrollOffset()) {
                val x = mFlingScroller.currX - mLastFlingX
                val y = mFlingScroller.currY - mLastFlingY
                mLastFlingX = mFlingScroller.currX
                mLastFlingY = mFlingScroller.currY
                mTranslateX += x
                mTranslateY += y
                endAnim = false
            }
            if (mRotateScroller.computeScrollOffset()) {
                mDegrees = mRotateScroller.currX.toFloat()
                endAnim = false
            }
            if (mClipScroller.computeScrollOffset() || mClip != null) {
                val sx = mClipScroller.currX / 10000f
                val sy = mClipScroller.currY / 10000f
                mTmpMatrix.setScale(sx, sy, (mImgRect.left + mImgRect.right) / 2, mClipCalc?.calculateTop() ?: 0f)
                mTmpMatrix.mapRect(mClipRect, mImgRect)
                if (sx == 1f) {
                    mClipRect.left = mWidgetRect.left
                    mClipRect.right = mWidgetRect.right
                }
                if (sy == 1f) {
                    mClipRect.top = mWidgetRect.top
                    mClipRect.bottom = mWidgetRect.bottom
                }
                mClip = mClipRect
            }
            if (!endAnim) {
                applyAnim()
                postExecute()
            } else {
                isRunning = false
                // 修复动画结束后边距有些空隙，
                var needFix = false
                if (imgLargeWidth) {
                    if (mImgRect.left > 0) {
                        mTranslateX -= mImgRect.left.toInt()
                    } else if (mImgRect.right < mWidgetRect.width()) {
                        mTranslateX -= (mWidgetRect.width() - mImgRect.right).toInt()
                    }
                    needFix = true
                }
                if (imgLargeHeight) {
                    if (mImgRect.top > 0) {
                        mTranslateY -= mImgRect.top.toInt()
                    } else if (mImgRect.bottom < mWidgetRect.height()) {
                        mTranslateY -= (mWidgetRect.height() - mImgRect.bottom).toInt()
                    }
                    needFix = true
                }
                if (needFix) {
                    applyAnim()
                }
                invalidate()
                mCompleteCallBack?.run {
                    run()
                    mCompleteCallBack = null
                }
            }
        }

        private fun applyAnim() {
            mAnimMatrix.reset()
            mAnimMatrix.postTranslate(-mBaseRect.left, -mBaseRect.top)
            mAnimMatrix.postTranslate(mRotateCenter.x, mRotateCenter.y)
            mAnimMatrix.postTranslate(-mHalfBaseRectWidth, -mHalfBaseRectHeight)
            mAnimMatrix.postRotate(mDegrees, mRotateCenter.x, mRotateCenter.y)
            mAnimMatrix.postScale(mScale, mScale, mScaleCenter.x, mScaleCenter.y)
            mAnimMatrix.postTranslate(mTranslateX.toFloat(), mTranslateY.toFloat())
            executeTranslate()
        }

        private fun postExecute() {
            if (isRunning) {
                post(this)
            }
        }

        init {
            val ctx = context
            mTranslateScroller = OverScroller(ctx, mInterpolatorProxy)
            mScaleScroller = Scroller(ctx, mInterpolatorProxy)
            mFlingScroller = OverScroller(ctx, mInterpolatorProxy)
            mClipScroller = Scroller(ctx, mInterpolatorProxy)
            mRotateScroller = Scroller(ctx, mInterpolatorProxy)
        }
    }

    val info: Info
        get() {
            val rect = RectF()
            val p = IntArray(2)
            getLocation(this, p)
            rect[p[0] + mImgRect.left, p[1] + mImgRect.top, p[0] + mImgRect.right] = p[1] + mImgRect.bottom
            return Info(rect,
                mImgRect,
                mWidgetRect,
                mBaseRect,
                mScreenCenter,
                mScale,
                mDegrees,
                mScaleType!!
            )
        }

    private fun reset() {
        mAnimMatrix.reset()
        executeTranslate()
        mScale = 1f
        mTranslateX = 0
        mTranslateY = 0
    }

    interface ClipCalculate {
        fun calculateTop(): Float
    }

    inner class START : ClipCalculate {
        override fun calculateTop(): Float {
            return mImgRect.top
        }
    }

    inner class END : ClipCalculate {
        override fun calculateTop(): Float {
            return mImgRect.bottom
        }
    }

    inner class OTHER : ClipCalculate {
        override fun calculateTop(): Float {
            return (mImgRect.top + mImgRect.bottom) / 2
        }
    }

    /**
     * 在PhotoView内部还没有图片的时候同样可以调用该方法
     * 此时并不会播放动画，当给PhotoView设置图片后会自动播放动画。
     *
     * 若等待时间过长也没有给控件设置图片，则会忽略该动画，若要再次播放动画则需要重新调用该方法
     * (等待的时间默认500毫秒，可以通过setMaxAnimFromWaiteTime(int)设置最大等待时间)
     */
    fun animFrom(info: Info) {
        if (isInit) {
            reset()
            val scaleX = info.imgRect.width() / info.imgRect.width()
            val scaleY = info.imgRect.height() / info.imgRect.height()
            val scale = if (scaleX < scaleY) scaleX else scaleY
            val ocx = info.rect.left + info.rect.width() / 2
            val ocy = info.rect.top + info.rect.height() / 2
            val mcx = info.rect.left + info.rect.width() / 2
            val mcy = info.rect.top + info.rect.height() / 2
            mAnimMatrix.reset()
            mAnimMatrix.postTranslate(ocx - mcx, ocy - mcy)
            mAnimMatrix.postScale(scale, scale, ocx, ocy)
            mAnimMatrix.postRotate(info.degrees, ocx, ocy)
            executeTranslate()
            mScaleCenter[ocx] = ocy
            mRotateCenter[ocx] = ocy
            mTranslate.withTranslate(0, 0, (-(ocx - mcx)).toInt(), (-(ocy - mcy)).toInt())
            mTranslate.withScale(scale, 1f)
            mTranslate.withRotate(info.degrees.toInt(), 0)
            if (info.widgetRect.width() < info.imgRect.width() || info.widgetRect.height() < info.imgRect.height()) {
                var clipX = info.widgetRect.width() / info.imgRect.width()
                var clipY = info.widgetRect.height() / info.imgRect.height()
                clipX = if (clipX > 1) 1f else clipX
                clipY = if (clipY > 1) 1f else clipY
                val c = if (info.scaleType == ScaleType.FIT_START) START() else if (info.scaleType == ScaleType.FIT_END) END() else OTHER()
                mTranslate.withClip(clipX, clipY, 1 - clipX, 1 - clipY, animDuring / 3, c)
                mTmpMatrix.setScale(clipX, clipY,(mImgRect.left + mImgRect.right) / 2, c.calculateTop())
                mTmpMatrix.mapRect(mTranslate.mClipRect, mImgRect)
                mClip = mTranslate.mClipRect
            }
            mTranslate.start()
        } else {
            mFromInfo = info
            mInfoTime = System.currentTimeMillis()
        }
    }

    fun animTo(info: Info, completeCallBack: Runnable?) {
        if (isInit) {
            mTranslate.stop()
            mTranslateX = 0
            mTranslateY = 0
            val tcx = info.rect.left + info.rect.width() / 2
            val tcy = info.rect.top + info.rect.height() / 2
            mScaleCenter[mImgRect.left + mImgRect.width() / 2] = mImgRect.top + mImgRect.height() / 2
            mRotateCenter.set(mScaleCenter)
            // 将图片旋转回正常位置，用以计算
            mAnimMatrix.postRotate(-mDegrees, mScaleCenter.x, mScaleCenter.y)
            mAnimMatrix.mapRect(mImgRect, mBaseRect)
            // 缩放
            val scaleX = info.imgRect.width() / mBaseRect.width()
            val scaleY = info.imgRect.height() / mBaseRect.height()
            val scale = if (scaleX > scaleY) scaleX else scaleY
            mAnimMatrix.postRotate(mDegrees, mScaleCenter.x, mScaleCenter.y)
            mAnimMatrix.mapRect(mImgRect, mBaseRect)
            mDegrees %= 360
            mTranslate.withTranslate(0, 0, (tcx - mScaleCenter.x).toInt(), (tcy - mScaleCenter.y).toInt())
            mTranslate.withScale(mScale, scale)
            mTranslate.withRotate(mDegrees.toInt(), info.degrees.toInt(), animDuring * 2 / 3)
            if (info.widgetRect.width() < info.rect.width() || info.widgetRect.height() < info.rect.height()) {
                var clipX = info.widgetRect.width() / info.rect.width()
                var clipY = info.widgetRect.height() / info.rect.height()
                clipX = if (clipX > 1) 1f else clipX
                clipY = if (clipY > 1) 1f else clipY
                val cx = clipX
                val cy = clipY
                val c = if (info.scaleType == ScaleType.FIT_START) START() else if (info.scaleType == ScaleType.FIT_END) END() else OTHER()
                postDelayed({ mTranslate.withClip(1f, 1f, -1 + cx, -1 + cy, animDuring / 2, c) }, animDuring / 2.toLong())
            }
            mCompleteCallBack = completeCallBack
            mTranslate.start()
        }
    }

    fun rotate(degrees: Float) {
        mDegrees += degrees
        val centerX = (mWidgetRect.left + mWidgetRect.width() / 2).toInt()
        val centerY = (mWidgetRect.top + mWidgetRect.height() / 2).toInt()
        mAnimMatrix.postRotate(degrees, centerX.toFloat(), centerY.toFloat())
        executeTranslate()
    }

    companion object {
        var MAX_OVER_SCROLL = 0
        var MAX_FLING_OVER_SCROLL = 0
        var MAX_OVER_RESISTANCE = 0
        var MAX_ANIM_FROM_WAITE = 500

        private const val MIN_ROTATE = 35
        /**
         * 获取默认的动画持续时间
         */
        const val defaultAnimDuring = 340
        private const val MAX_SCALE = 2.5f
        private fun getDrawableWidth(d: Drawable): Int {
            var width = d.intrinsicWidth
            if (width <= 0) {
                width = d.minimumWidth
            }
            if (width <= 0) {
                width = d.bounds.width()
            }
            return width
        }

        private fun getDrawableHeight(d: Drawable): Int {
            var height = d.intrinsicHeight
            if (height <= 0) {
                height = d.minimumHeight
            }
            if (height <= 0) {
                height = d.bounds.height()
            }
            return height
        }

        fun getImageViewInfo(imgView: ImageView): Info {
            val p = IntArray(2)
            getLocation(imgView, p)
            val drawable = imgView.drawable
            val matrix = imgView.imageMatrix
            val width = getDrawableWidth(drawable)
            val height =
                getDrawableHeight(drawable)
            val imgRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
            matrix.mapRect(imgRect)
            val rect = RectF(p[0] + imgRect.left, p[1] + imgRect.top, p[0] + imgRect.right, p[1] + imgRect.bottom)
            val widgetRect = RectF(0f, 0f, imgView.width.toFloat(), imgView.height.toFloat())
            val baseRect = RectF(widgetRect)
            val screenCenter = PointF(widgetRect.width() / 2, widgetRect.height() / 2)
            return Info(rect, imgRect, widgetRect, baseRect, screenCenter, 1f, 0f, imgView.scaleType)
        }

        private fun getLocation(target: View, position: IntArray) {
            position[0] += target.left
            position[1] += target.top
            var viewParent = target.parent
            while (viewParent is View) {
                val view = viewParent as View
                if (view.id == R.id.content) {
                    return
                }
                position[0] -= view.scrollX
                position[1] -= view.scrollY
                position[0] += view.left
                position[1] += view.top
                viewParent = view.parent
            }
            position[0] = (position[0] + 0.5f).toInt()
            position[1] = (position[1] + 0.5f).toInt()
        }
    }
}