package com.bese.widget.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import com.bese.R
import com.bese.widget.button.BorderTextButton
import com.blankj.utilcode.util.SizeUtils

/**
 * 通用增强弹窗
 *      效果：仿iOS系统弹窗界面
 *      适用范围：自定义简单弹窗-标题+消息+强化点击引导
 *      Link：自定义View弹窗  ViewDialog，可使用自定义View
 */
class EnhanceDialog(private val mCtx: Context?) {

    @ColorInt private var backgroundColor: Int = Color.WHITE
    private var backgroundDim: Float = 0.5f

    private var title: String? = null
    private var titleTextSize: Float = getDp(15f).toFloat()
    @ColorInt private var titleTextColor: Int = Color.BLACK
    private var titleGravity: Int = Gravity.CENTER
    private var titleIsBold: Boolean = false

    private var message: String? = null
    private var messageTextSize: Float = getDp(14f).toFloat()
    @ColorInt private var messageTextColor: Int = Color.BLACK
    private var messageGravity: Int = Gravity.CENTER

    @ColorInt private var bottomLineColor: Int = Color.parseColor("#cccccc")

    private var cancelText: String? = "取消"
    @ColorInt private var cancelTextColor: Int = Color.parseColor("#999999")
    @ColorInt private var cancelTextBgColor: Int = Color.parseColor("#f8f8f8")
    @ColorInt private var cancelTextBgBorder: Int = Color.parseColor("#999999")

    private var confirmText: String? = "删除"
    @ColorInt private var confirmTextColor: Int = Color.parseColor("#ffffff")
    @ColorInt private var confirmTextBgColor: Int = Color.parseColor("#cc3333")
    @ColorInt private var confirmTextBgBorder: Int = Color.parseColor("#ff0000")

    private var animationStyle: Int = -1

    private var dialogLeftMargin: Int = getDp(50f)
    private var dialogRightMargin: Int = getDp(50f)

    private var backgroundDrawable: Drawable? = null

    private var hideDialogWhileClickRight: Boolean = true

    private var dismissClickOutside: Boolean = false

    private var cancelable: Boolean = true

    private var listener: DialogListener? = null

    /**
     * 创建XDialog
     */
    private var mDialog: Dialog? = null
    private var dialogView: View? = null
    private var customLayout: LinearLayout? = null

    private var titleTv: TextView? = null
    private var msgTv: TextView? = null
    private var leftBtn: BorderTextButton? = null
    private var rightBtn: BorderTextButton? = null
    private var btnLine: View? = null

    private fun getDp(dpValue: Float): Int {
        return SizeUtils.dp2px(dpValue)
    }

    /**
     * 设置Dialog的背景渲染色
     */
    fun setBackgroundColor(@ColorInt color: Int): EnhanceDialog {
        this.backgroundColor = color
        return this
    }

    /**
     * 设置Dialog的背景渲染色，ColorInt
     */
    fun setBackgroundColorRaw(@ColorInt color: Int): EnhanceDialog {
        this.backgroundColor = color
        return this
    }

    /**
     * 设置Dialog的背景渲染色
     */
    fun setBackgroundDim(dim: Float): EnhanceDialog {
        this.backgroundDim = dim
        return this
    }

    /**
     * 设置Dialog的标题
     */
    fun setTitle(title: String?): EnhanceDialog {
        this.title = title
        return this
    }

    /**
     * 设置Dialog的标题文字大小
     */

    fun setTitleTextSize(size: Float): EnhanceDialog {
        this.titleTextSize = size
        return this
    }

    /**
     * 设置Dialog的标题文字颜色
     */
    fun setTitleTextColor(@ColorInt color: Int): EnhanceDialog {
        this.titleTextColor = color
        return this
    }

    /**
     * 设置Dialog的标题对齐方式
     */
    fun setTitleGravity(gravity: Int): EnhanceDialog {
        this.titleGravity = gravity
        return this
    }

    /**
     * 设置Dialog的标题对齐方式
     */
    fun setTitleBold(isBold: Boolean): EnhanceDialog {
        this.titleIsBold = isBold
        return this
    }

    /**
     * 设置Dialog的内容
     * 最好不要直接第一个设置Message，建议应先设置Title，保证文字距离顶部的距离
     */
    fun setMessage(msg: String?): EnhanceDialog {
        this.message = msg
        return this
    }

    /**
     * 设置Dialog的消息文字大小
     */
    fun setMessageTextSize(size: Float): EnhanceDialog {
        this.messageTextSize = size
        return this
    }

    /**
     * 设置Dialog的消息文字颜色
     */
    fun setMessageTextColor(@ColorInt color: Int): EnhanceDialog {
        this.messageTextColor = color
        return this
    }

    /**
     * 设置Dialog的消息对齐方式
     */
    fun setMessageGravity(gravity: Int): EnhanceDialog {
        this.messageGravity = gravity
        return this
    }

    /**
     * 设置Dialog的底部按钮分隔线颜色
     */
    fun setBottomLineColor(@ColorInt lineColor: Int): EnhanceDialog {
        this.bottomLineColor = lineColor
        return this
    }

    /**
     * 设置Dialog的取消按钮文案
     */
    fun setCancelText(cancelTxt: String?): EnhanceDialog {
        this.cancelText = cancelTxt
        return this
    }

    /**
     * 设置Dialog的取消按钮文字颜色
     */
    fun setCancelTextColor(@ColorInt color: Int): EnhanceDialog {
        this.cancelTextColor = color
        return this
    }

    /**
     * 设置Dialog的取消按钮背景颜色
     */
    fun setCancelTextBgColor(@ColorInt color: Int): EnhanceDialog {
        this.cancelTextBgColor = color
        return this
    }

    /**
     * 设置Dialog的取消按钮背景边框色
     */
    fun setCancelTextBgBorder(@ColorInt color: Int): EnhanceDialog {
        this.cancelTextBgBorder = color
        return this
    }

    /**
     * 设置Dialog的确认按钮文案
     */
    fun setEnterText(enterTxt: String?): EnhanceDialog {
        this.confirmText = enterTxt
        return this
    }

    /**
     * 设置Dialog的确认按钮文字颜色
     */
    fun setEnterTextColor(@ColorInt color: Int): EnhanceDialog {
        this.confirmTextColor = color
        return this
    }

    /**
     * 设置Dialog的确认按钮背景颜色
     */
    fun setEnterTextBgColor(@ColorInt color: Int): EnhanceDialog {
        this.cancelTextBgColor = color
        return this
    }

    /**
     * 设置Dialog的确认按钮背景边框色
     */
    fun setEnterTextBgBorder(@ColorInt color: Int): EnhanceDialog {
        this.cancelTextBgBorder = color
        return this
    }

    /**
     * 设置Dialog的监听
     */
    fun setListener(listener: DialogListener): EnhanceDialog {
        this.listener = listener
        return this
    }

    /**
     * 设置Dialog的左屏距
     */
    fun setLeftScreenMargin(margin: Float): EnhanceDialog {
        this.dialogLeftMargin = getDp(margin)
        return this
    }

    /**
     * 设置Dialog的右屏距
     */
    fun setRightScreenMargin(margin: Float): EnhanceDialog {
        this.dialogRightMargin = getDp(margin)
        return this
    }

    /**
     * 设置Dialog的根背景
     */
    fun setBackgroundDrawable(drawable: Drawable): EnhanceDialog {
        this.backgroundDrawable = drawable
        return this
    }

    /**
     * 设置确定按钮单击后是否弹窗消失
     */
    fun setRightClickHideDialog(hide: Boolean): EnhanceDialog {
        this.hideDialogWhileClickRight = hide
        return this
    }

    /**
     * 设置按钮外部蒙层弹窗是否消失
     */
    fun setDismissClickOutside(hide: Boolean): EnhanceDialog {
        this.dismissClickOutside = hide
        return this
    }

    /**
     * 设置返回按钮是否让弹窗消失
     */
    fun setDialogCancelable(cancelable: Boolean): EnhanceDialog {
        this.cancelable = cancelable
        return this
    }

    /**
     * 设置弹窗动画样式：R.style.toast_animation
     */
    fun setAnimationStyle(animationStyle: Int): EnhanceDialog {
        this.animationStyle = animationStyle
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
            dialogView = LayoutInflater.from(mCtx).inflate(R.layout.dialog_enhance, null)
            // 绑定控件
            dialogView?.run {
                customLayout = findViewById(R.id.layout_enhance)
                titleTv = findViewById(R.id.dialog_enhance_title)
                msgTv = findViewById(R.id.dialog_enhance_msg)
                leftBtn = findViewById(R.id.btn_enhance_left)
                rightBtn = findViewById(R.id.btn_enhance_right)
                btnLine = findViewById(R.id.btn_space)
            }
            customLayout?.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            backgroundDrawable?.run { customLayout?.background = backgroundDrawable }

            if (dialogLeftMargin != 0 || dialogRightMargin != 0) {
                val dialogParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                dialogParam.setMargins(dialogLeftMargin, 0, dialogRightMargin, 0)
                customLayout?.layoutParams = dialogParam
            }

            // 标题相关设置：可设置是否加粗，无标题隐藏
            if (title?.isNotEmpty() == true) {
                titleTv?.run {
                    visibility = View.VISIBLE
                    text = title ?: ""
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize)
                    setTextColor(titleTextColor)
                    gravity = titleGravity
                    paint?.isFakeBoldText = titleIsBold
                }
            } else {
                titleTv?.visibility = View.GONE
            }

            // 消息内容相关设置：无内容自动隐藏
            if (message?.isNotEmpty() == true) {
                msgTv?.run {
                    visibility = View.VISIBLE
                    text = message ?: ""
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, messageTextSize)
                    setTextColor(messageTextColor)
                    gravity = messageGravity
                }
            } else {
                msgTv?.visibility = View.GONE
            }

            // 取消按钮相关设置：无文字即隐藏，连带中间分割线一并隐藏
            if (cancelText?.isNotEmpty() == true) {
                leftBtn?.run {
                    visibility = View.VISIBLE
                    text = cancelText
                    setTextColor(cancelTextColor)
                    setBgColor(cancelTextBgColor)
                    setBorderColor(cancelTextBgBorder)
                    setOnClickListener {
                        listener?.doCancel(mDialog)
                        dismiss()
                    }
                }
            } else {
                leftBtn?.visibility = View.GONE
                btnLine?.visibility = View.GONE
            }

            // 确定按钮相关设置：无文字即隐藏，连带中间分割线一并隐藏
            if (confirmText?.isNotEmpty() == true) {
                rightBtn?.run {
                    visibility = View.VISIBLE
                    text = confirmText
                    setTextColor(confirmTextColor)
                    setBgColor(confirmTextBgColor)
                    setBorderColor(confirmTextBgBorder)
                    setOnClickListener {
                        listener?.doEnter(mDialog)
                        if (hideDialogWhileClickRight) { dismiss() }
                    }
                }
            } else {
                rightBtn?.visibility = View.GONE
                btnLine?.visibility = View.GONE
            }

            // 绑定视图
            dialogView?.run { setContentView(this) }

            showDialog()
        }
    }

    fun hideDialog() {
        mDialog?.dismiss()
    }

    fun showDialog() {
        mDialog?.run {
            if (!isShowing) {
                try {
                    show()          // dialog展示
                } catch (e: Exception) {
                    Log.e("EnhanceDialog-Error", "${e.message}")
                }
            }
        }
    }

}
