package com.bese.view

import android.content.Context
import android.content.res.ColorStateList
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import com.bese.R
import com.bese.util.TimeUtil
import com.blankj.utilcode.constant.TimeConstants
import kotlinx.android.synthetic.main.time_count_down.view.*
import org.jetbrains.anko.textColor

/**
 * 倒计时View
 */
class CountDownTimerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var timer: CountDownTimer? = null
    var onFinishListener: OnFinishListener? = null

    interface OnFinishListener {
        fun onFinish()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        LayoutInflater.from(context).inflate(R.layout.time_count_down, this, true)
    }

    fun resetTimer(millisInFuture: Long?, countDownInterval: Long = 1000L) {
        timer?.cancel()
        millisInFuture?.run {
            timer = object : CountDownTimer(this, countDownInterval) {
                override fun onFinish() { onFinishListener?.onFinish() }
                override fun onTick(millisUntilFinished: Long) { updateTime(millisUntilFinished) }
            }
            timer?.start()
        }
    }

    /**
     * 更新时间
     */
    fun updateTime(millis: Long) {
        val HOUR = millis / TimeConstants.HOUR
        val MIN = (millis - HOUR * TimeConstants.HOUR) / TimeConstants.MIN
        val SEC = (millis - HOUR * TimeConstants.HOUR - MIN * TimeConstants.MIN) / TimeConstants.SEC

        hour?.text = TimeUtil.getTimeCover(HOUR)
        minute?.text = TimeUtil.getTimeCover(MIN)
        second?.text = TimeUtil.getTimeCover(SEC)

    }

    fun cancel() {
        timer?.cancel()
        timer = null
    }

    fun setTimerBackground(@ColorInt colorInt: Int) {
        hour?.backgroundTintList = ColorStateList.valueOf(colorInt)
        minute?.backgroundTintList = ColorStateList.valueOf(colorInt)
        second?.backgroundTintList = ColorStateList.valueOf(colorInt)
    }

    fun setTimerTextColor(@ColorInt colorInt: Int) {
        hour?.textColor = colorInt
        minute?.textColor = colorInt
        second?.textColor = colorInt
    }

    fun setTimerColonColor(@ColorInt colorInt: Int) {
        hour_colon?.textColor = colorInt
        minute_colon?.textColor = colorInt
    }
}
