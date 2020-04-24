package com.bese.view.photoview

import android.view.MotionEvent
import kotlin.math.abs
import kotlin.math.atan

/**
 * 旋转手势
 */
class RotateGestureDetector(private val mListener: OnRotateListener) {
    private var mPrevSlope = 0f
    private var mCurrSlope = 0f
    private var x1 = 0f
    private var y1 = 0f
    private var x2 = 0f
    private var y2 = 0f
    fun onTouchEvent(event: MotionEvent) {
        val action = event.actionMasked
        when (action) {
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP -> if (event.pointerCount == 2) {
                mPrevSlope = calculateSlope(event)
            }
            MotionEvent.ACTION_MOVE -> if (event.pointerCount > 1) {
                mCurrSlope = calculateSlope(event)
                val currDegrees = Math.toDegrees(atan(mCurrSlope.toDouble()))
                val prevDegrees = Math.toDegrees(atan(mPrevSlope.toDouble()))
                val deltaSlope = currDegrees - prevDegrees
                if (abs(deltaSlope) <= MAX_DEGREES_STEP) {
                    mListener.onRotate(deltaSlope.toFloat(), (x2 + x1) / 2, (y2 + y1) / 2)
                }
                mPrevSlope = mCurrSlope
            }
            else -> { }
        }
    }

    private fun calculateSlope(event: MotionEvent): Float {
        x1 = event.getX(0)
        y1 = event.getY(0)
        x2 = event.getX(1)
        y2 = event.getY(1)
        return (y2 - y1) / (x2 - x1)
    }

    companion object {
        private const val MAX_DEGREES_STEP = 120
    }

}