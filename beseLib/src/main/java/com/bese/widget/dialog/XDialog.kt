package com.bese.widget.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import com.bese.R

/**
 * 通用弹窗工具
 *      效果：仿iOS系统弹窗界面
 *      适用范围：自定义简单弹窗-标题+消息
 */
class XDialog(private val mCtx: Context?) {

    companion object {
        var TIMER_FLAG = "%%%"
        var TIMER_TYPE_ENTER = 0
        var TIMER_TYPE_CANCEL = 1
        var TIMER_TYPE_MESSAGE = 2
        var TIMER_TYPE_TITLE = 3
    }

    @ColorInt private var backgroundColor: Int = Color.WHITE
    private var backgroundDim: Float = 0.5f

    private var title: String? = null
    private var titleTextSize: Float = getSp(15f).toFloat()
    @ColorInt private var titleTextColor: Int = Color.BLACK
    private var titleGravity: Int = Gravity.CENTER
    private var titleIsBold: Boolean = true

    private var message: String? = null
    private var messageTextSize: Float = getSp(14f).toFloat()
    @ColorInt private var messageTextColor: Int = Color.BLACK
    private var messageGravity: Int = Gravity.CENTER

    @ColorInt private var bottomLineColor: Int = Color.parseColor("#cccccc")

    private var cancelText: String? = "取消"
    @ColorInt private var cancelTextColor: Int = Color.parseColor("#333333")

    private var confirmText: String? = "确定"
    @ColorInt private var confirmTextColor: Int = Color.parseColor("#0099ff")

    private var animationStyle: Int = -1

    private var dialogLeftMargin: Int = getDp(40f)
    private var dialogRightMargin: Int = getDp(40f)

    private var backgroundDrawable: Drawable? = null

    private var hideDialogWhileClickRight: Boolean = true

    private var dismissClickOutside: Boolean = false

    private var cancelable: Boolean = true

    private var listener: DialogListener? = null

    private var totalMs: Long = 0
    private var timerType: Int = -1
    private var timer: CountDownTimer? = null
    private var timerSplit = arrayOf<String>()

    /**
     * 创建XDialog
     */
    private var mDialog: Dialog? = null
    private var dialogView: View? = null
    private var customLayout: LinearLayout? = null

    private var titleTv: TextView? = null
    private var msgTv: TextView? = null
    private var leftBtn: TextView? = null
    private var rightBtn: TextView? = null
    private var bottomLine: View? = null
    private var btnLine: View? = null

    private fun getDp(dpValue: Float): Int {
        mCtx?.run {
            val scale = resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
        return (dpValue * 3).toInt()
    }

    private fun getSp(spValue: Float): Int {
        mCtx?.run {
            // SP取用DP值，防止系统字体变大
            val scale = resources.displayMetrics.density
            return (spValue * scale + 0.5f).toInt()
        }
        return (spValue * 3).toInt()
    }

    /**
     * 设置Dialog的背景渲染色
     */
    fun setBackgroundColor(@ColorInt color: Int): XDialog {
        this.backgroundColor = color
        return this
    }

    /**
     * 设置Dialog的背景渲染色，ColorInt
     */
    fun setBackgroundColorRaw(@ColorInt color: Int): XDialog {
        this.backgroundColor = color
        return this
    }

    /**
     * 设置Dialog的背景渲染色
     */
    fun setBackgroundDim(dim: Float): XDialog {
        this.backgroundDim = dim
        return this
    }

    /**
     * 设置Dialog的标题
     */
    fun setTitle(title: String?): XDialog {
        this.title = title
        return this
    }

    fun getTitle(): String {
        return title ?: ""
    }

    /**
     * 设置Dialog的标题文字大小
     */

    fun setTitleTextSize(size: Float): XDialog {
        this.titleTextSize = size
        return this
    }

    /**
     * 设置Dialog的标题文字颜色
     */
    fun setTitleTextColor(@ColorInt color: Int): XDialog {
        this.titleTextColor = color
        return this
    }

    /**
     * 设置Dialog的标题对齐方式
     */
    fun setTitleGravity(gravity: Int): XDialog {
        this.titleGravity = gravity
        return this
    }

    /**
     * 设置Dialog的标题对齐方式
     */
    fun setTitleBold(isBold: Boolean): XDialog {
        this.titleIsBold = isBold
        return this
    }

    /**
     * 设置Dialog的内容
     * 最好不要直接第一个设置Message，建议应先设置Title，保证文字距离顶部的距离
     */
    fun setMessage(msg: String?): XDialog {
        this.message = msg
        return this
    }

    fun getMessage(): String {
        return message ?: ""
    }

    /**
     * 设置Dialog的消息文字大小
     */
    fun setMessageTextSize(size: Float): XDialog {
        this.messageTextSize = size
        return this
    }

    /**
     * 设置Dialog的消息文字颜色
     */
    fun setMessageTextColor(@ColorInt color: Int): XDialog {
        this.messageTextColor = color
        return this
    }

    /**
     * 设置Dialog的消息对齐方式
     */
    fun setMessageGravity(gravity: Int): XDialog {
        this.messageGravity = gravity
        return this
    }

    /**
     * 设置Dialog的底部按钮分隔线颜色
     */
    fun setBottomLineColor(@ColorInt lineColor: Int): XDialog {
        this.bottomLineColor = lineColor
        return this
    }

    /**
     * 设置Dialog的取消按钮文案
     */
    fun setCancelText(cancelTxt: String?): XDialog {
        this.cancelText = cancelTxt
        return this
    }

    fun getCancelText(): String {
        return cancelText ?: ""
    }

    /**
     * 设置Dialog的取消按钮文字颜色
     */
    fun setCancelTextColor(@ColorInt color: Int): XDialog {
        this.cancelTextColor = color
        return this
    }

    /**
     * 设置Dialog的确认按钮文案
     */
    fun setEnterText(enterTxt: String?): XDialog {
        this.confirmText = enterTxt
        return this
    }

    fun getEnterText(): String {
        return confirmText ?: ""
    }

    /**
     * 设置Dialog的确认按钮文字颜色
     */
    fun setEnterTextColor(@ColorInt color: Int): XDialog {
        this.confirmTextColor = color
        return this
    }

    /**
     * 设置Dialog的监听
     */
    fun setListener(listener: DialogListener): XDialog {
        this.listener = listener
        return this
    }

    /**
     * 设置Dialog的左屏距
     */
    fun setLeftScreenMargin(margin: Float): XDialog {
        this.dialogLeftMargin = getDp(margin)
        return this
    }

    /**
     * 设置Dialog的右屏距
     */
    fun setRightScreenMargin(margin: Float): XDialog {
        this.dialogRightMargin = getDp(margin)
        return this
    }

    /**
     * 设置Dialog的根背景
     */
    fun setBackgroundDrawable(drawable: Drawable): XDialog {
        this.backgroundDrawable = drawable
        return this
    }

    /**
     * 设置确定按钮单击后是否弹窗消失
     */
    fun setRightClickHideDialog(hide: Boolean): XDialog {
        this.hideDialogWhileClickRight = hide
        return this
    }

    /**
     * 设置按钮外部蒙层弹窗是否消失
     */
    fun setDismissClickOutside(hide: Boolean): XDialog {
        this.dismissClickOutside = hide
        return this
    }

    /**
     * 设置返回按钮是否让弹窗消失
     */
    fun setDialogCancelable(cancelable: Boolean): XDialog {
        this.cancelable = cancelable
        return this
    }

    /**
     * 设置弹窗动画样式：R.style.toast_animation
     */
    fun setAnimationStyle(animationStyle: Int): XDialog {
        this.animationStyle = animationStyle
        return this
    }

    /** 设置倒计时，必须在设置Message之后  */
    fun setTimer(totalTimer: Long, interval: Long, timerType: Int? = TIMER_TYPE_ENTER,
                 @ColorInt timerColor: Int = Color.parseColor("#e64545")): XDialog {
        timer?.cancel()
        totalMs = totalTimer
        this.timerType = timerType ?: -1
        timer = object : CountDownTimer(totalTimer, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val sec = millisUntilFinished / 1000
                when (timerType) {
                    TIMER_TYPE_ENTER -> {
                        if (!TextUtils.isEmpty(confirmText)) {
                            val span = SpannableStringBuilder()
                            if (timerSplit.size > 1) {
                                span.append(timerSplit[0])
                                if (timerColor != 0) {
                                    span.append(sec.toString(), ForegroundColorSpan(timerColor), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                } else {
                                    span.append(sec.toString())
                                }
                                span.append(timerSplit[1])
                            } else {
                                span.append(confirmText)
                            }
                            rightBtn?.text = span
                        } else {
                            rightBtn?.text = ""
                        }
                    }
                    TIMER_TYPE_CANCEL -> {
                        if (!TextUtils.isEmpty(cancelText)) {
                            val span = SpannableStringBuilder()
                            if (timerSplit.size > 1) {
                                span.append(timerSplit[0])
                                if (timerColor != 0) {
                                    span.append(sec.toString(), ForegroundColorSpan(timerColor), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                } else {
                                    span.append(sec.toString())
                                }
                                span.append(timerSplit[1])
                            } else {
                                span.append(cancelText)
                            }
                            leftBtn?.text = span
                        } else {
                            leftBtn?.text = ""
                        }
                    }
                    TIMER_TYPE_MESSAGE -> {
                        if (!TextUtils.isEmpty(message)) {
                            val span = SpannableStringBuilder()
                            if (timerSplit.size > 1) {
                                span.append(timerSplit[0])
                                if (timerColor != 0) {
                                    span.append(sec.toString(), ForegroundColorSpan(timerColor), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                } else {
                                    span.append(sec.toString())
                                }
                                span.append(timerSplit[1])
                            } else {
                                span.append(message)
                            }
                            msgTv?.text = span
                        } else {
                            msgTv?.text = ""
                        }
                    }
                    TIMER_TYPE_TITLE -> {
                        if (!TextUtils.isEmpty(title)) {
                            val span = SpannableStringBuilder()
                            if (timerSplit.size > 1) {
                                span.append(timerSplit[0])
                                if (timerColor != 0) {
                                    span.append(sec.toString(), ForegroundColorSpan(timerColor), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                } else {
                                    span.append(sec.toString())
                                }
                                span.append(timerSplit[1])
                            } else {
                                span.append(title)
                            }
                            titleTv?.text = span
                        } else {
                            titleTv?.text = ""
                        }
                    }
                    else -> { }
                }
            }

            override fun onFinish() {
                hideDialog()
            }
        }
        return this
    }

    fun build() {
        // 自定义视图
        if (mDialog == null && mCtx != null) {
            mDialog = Dialog(mCtx, R.style.XDialog)
        }
        mDialog?.run {
            setCanceledOnTouchOutside(dismissClickOutside)
            setCancelable(cancelable)
            window?.run {
                decorView.setPadding(0, 0, 0, 0)
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setDimAmount(backgroundDim)
                if (animationStyle > 0) setWindowAnimations(animationStyle)
            }
            dialogView = LayoutInflater.from(mCtx).inflate(R.layout.dialog_x, null)
            // 绑定控件
            dialogView?.run {
                customLayout = findViewById(R.id.layout_custom)
                titleTv = findViewById(R.id.dialog_custom_title)
                msgTv = findViewById(R.id.dialog_custom_msg)
                leftBtn = findViewById(R.id.dialog_custom_left)
                rightBtn = findViewById(R.id.dialog_custom_right)
                bottomLine = findViewById(R.id.bottom_line)
                btnLine = findViewById(R.id.dialog_custom_btn_line)
            }
            customLayout?.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            backgroundDrawable?.run { customLayout?.background = backgroundDrawable }
            bottomLine?.setBackgroundColor(bottomLineColor)
            btnLine?.setBackgroundColor(bottomLineColor)

            if (dialogLeftMargin != 0 || dialogRightMargin != 0) {
                val dialogParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                dialogParam.setMargins(dialogLeftMargin, 0, dialogRightMargin, 0)
                customLayout?.layoutParams = dialogParam
            }

            // 标题相关设置：可设置是否加粗，无标题隐藏
            if (title?.isNotEmpty() == true) {
                titleTv?.run {
                    visibility = View.VISIBLE
                    textSize = titleTextSize
                    setTextColor(titleTextColor)
                    gravity = titleGravity
                    paint?.isFakeBoldText = titleIsBold
                    if (timerType == TIMER_TYPE_TITLE) {
                        title?.let {
                            timerSplit = it.split(TIMER_FLAG).toTypedArray()
                            title = it.replace(TIMER_FLAG, (totalMs / 1000).toString())
                        }
                    }
                    text = title ?: ""
                }
            } else {
                titleTv?.visibility = View.GONE
            }

            // 消息内容相关设置：无内容自动隐藏
            if (message?.isNotEmpty() == true) {
                msgTv?.run {
                    visibility = View.VISIBLE
                    textSize = messageTextSize
                    setTextColor(messageTextColor)
                    gravity = messageGravity
                    if (timerType == TIMER_TYPE_MESSAGE) {
                        message?.let {
                            timerSplit = it.split(TIMER_FLAG).toTypedArray()
                            message = it.replace(TIMER_FLAG, (totalMs / 1000).toString())
                        }
                    }
                    text = message ?: ""
                }
            } else {
                msgTv?.visibility = View.GONE
            }

            // 取消按钮相关设置：无文字即隐藏，连带中间分割线一并隐藏
            if (cancelText?.isNotEmpty() == true) {
                leftBtn?.run {
                    visibility = View.VISIBLE
                    setTextColor(cancelTextColor)
                    setOnClickListener {
                        listener?.doCancel(mDialog)
                        dismiss()
                    }
                    if (timerType == TIMER_TYPE_CANCEL) {
                        cancelText?.let {
                            timerSplit = it.split(TIMER_FLAG).toTypedArray()
                            cancelText = it.replace(TIMER_FLAG, (totalMs / 1000).toString())
                        }
                    }
                    text = cancelText
                }
            } else {
                leftBtn?.visibility = View.GONE
                btnLine?.visibility = View.GONE
            }

            // 确定按钮相关设置：无文字即隐藏，连带中间分割线一并隐藏
            if (confirmText?.isNotEmpty() == true) {
                rightBtn?.run {
                    visibility = View.VISIBLE
                    setTextColor(confirmTextColor)
                    setOnClickListener {
                        listener?.doEnter(mDialog)
                        if (hideDialogWhileClickRight) { dismiss() }
                    }
                    if (timerType == TIMER_TYPE_ENTER) {
                        confirmText?.let {
                            timerSplit = it.split(TIMER_FLAG).toTypedArray()
                            confirmText = it.replace(TIMER_FLAG, (totalMs / 1000).toString())
                        }
                    }
                    text = confirmText
                }
            } else {
                rightBtn?.visibility = View.GONE
                btnLine?.visibility = View.GONE
            }

            timer?.start()

            // 绑定视图
            dialogView?.run { setContentView(this) }

            showDialog()
        }
    }

    fun hideDialog() {
        timer?.cancel()
        timer = null
        mDialog?.dismiss()
    }

    fun showDialog() {
        mDialog?.run {
            if (!isShowing) {
                try {
                    show()          // dialog展示
                } catch (e: Exception) {
                    // 内部处理异常，常见的是activity 销毁太早
                    Log.e("XDialog-Error", "${e.message}")
                }
            }
        }
    }

}
