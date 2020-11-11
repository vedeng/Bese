package com.bese.widget.dialog

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import com.bese.R

/**
 * 弹窗工具
 *      效果：展示自定义View的弹窗
 */
class ViewDialog(private val mCtx: Context?) {

    @ColorInt
    private var backgroundColor: Int = Color.WHITE
    private var backgroundDrawable: Drawable? = null
    private var backgroundDim: Float = 0.5f

    private var diyView: View? = null
    private var diyViewMarginTop: Int = getDp(0f)
    private var diyViewMarginBottom: Int = getDp(0f)
    private var diyViewMarginHorizontal: Int = getDp(0f)

    private var dialogLeftMargin: Int = getDp(40f)
    private var dialogRightMargin: Int = getDp(40f)

    /** Dialog动画 */
    private var animationStyle: Int = -1
    /** Dialog主题样式 */
    private var dialogTheme: Int = 0

    private var dismissClickOutside: Boolean = false

    private var cancelable: Boolean = true

    private var dismissListener: DismissListener? = null

    /**
     * 创建XDialog
     */
    private var mDialog: Dialog? = null
    private var dialogView: View? = null
    private var customLayout: LinearLayout? = null
    private var contentLayout: LinearLayout? = null

    private fun getDp(dpValue: Float): Int {
        mCtx?.run {
            val scale = resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
        return 0
    }

    /**
     * 设置Dialog的背景渲染色
     */
    fun setBackgroundColor(@ColorInt color: Int): ViewDialog {
        this.backgroundColor = color
        return this
    }

    /**
     * 设置Dialog的背景渲染色，ColorInt
     */
    fun setBackgroundColorRaw(@ColorInt color: Int): ViewDialog {
        this.backgroundColor = color
        return this
    }

    /**
     * 设置Dialog的背景渲染色
     */
    fun setBackgroundDim(dim: Float): ViewDialog {
        this.backgroundDim = dim
        return this
    }

    /**
     * 设置自定义布局
     * 设置后提供的左右屏幕边距会无效
     */
    fun setDiyView(diyView: View): ViewDialog {
        this.diyView = diyView
        return this
    }

    /**
     * 设置Dialog的自定义View竖直间距
     */
    fun setDiyViewMarginTop(margin: Float): ViewDialog {
        this.diyViewMarginTop = getDp(margin)
        return this
    }

    /**
     * 设置Dialog的自定义View竖直间距
     */
    fun setDiyViewMarginBottom(margin: Float): ViewDialog {
        this.diyViewMarginBottom = getDp(margin)
        return this
    }

    /**
     * 设置Dialog的自定义View水平间距
     */
    fun setDiyViewMarginHorizontal(margin: Float): ViewDialog {
        this.diyViewMarginHorizontal = getDp(margin)
        return this
    }

    /**
     * 设置Dialog的左屏距
     */
    fun setLeftScreenMargin(margin: Float): ViewDialog {
        this.dialogLeftMargin = getDp(margin)
        return this
    }

    /**
     * 设置Dialog的右屏距
     */
    fun setRightScreenMargin(margin: Float): ViewDialog {
        this.dialogRightMargin = getDp(margin)
        return this
    }

    /**
     * 设置Dialog的根背景
     */
    fun setBackgroundDrawable(drawable: Drawable): ViewDialog {
        this.backgroundDrawable = drawable
        return this
    }

    /**
     * 设置按钮外部蒙层弹窗是否消失
     */
    fun setDismissClickOutside(hide: Boolean): ViewDialog {
        this.dismissClickOutside = hide
        return this
    }

    /**
     * 设置返回按钮是否让弹窗消失
     */
    fun setDialogCancelable(cancelable: Boolean): ViewDialog {
        this.cancelable = cancelable
        return this
    }

    /**
     * 设置弹窗动画样式：R.anim.*
     */
    fun setAnimationStyle(animationStyle: Int): ViewDialog {
        this.animationStyle = animationStyle
        return this
    }

    /**
     * 设置弹窗主题样式：默认：R.style.XDialog
     */
    fun setDialogThemeStyle(theme: Int): ViewDialog {
        this.dialogTheme = theme
        return this
    }

    /**
     * 设置弹窗消失监听
     */
    fun setDismissListener(listener: DismissListener?) {
        this.dismissListener = listener
    }

    fun build() {
        // 自定义视图
        if (mDialog == null && mCtx != null) {
            mDialog = Dialog(mCtx, if (dialogTheme != 0) dialogTheme else R.style.XDialog)
        }
        mDialog?.run {
            setCanceledOnTouchOutside(false)
            setCancelable(true)
            window?.run {
                decorView.setPadding(0, 0, 0, 0)
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setDimAmount(backgroundDim)
                if (animationStyle > 0) setWindowAnimations(animationStyle)
            }
            dialogView = LayoutInflater.from(mCtx).inflate(R.layout.dialog_diy_view, null)
            // 绑定控件
            dialogView?.run {
                customLayout = findViewById(R.id.layout_diy_custom)
                contentLayout = findViewById(R.id.dialog_diy_content)
                customLayout?.backgroundTintList = ColorStateList.valueOf(backgroundColor)
            }

            backgroundDrawable?.run { customLayout?.background = backgroundDrawable }

            if (dialogLeftMargin != 0 || dialogRightMargin != 0) {
                val dialogParam = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                dialogParam.setMargins(dialogLeftMargin, 0, dialogRightMargin, 0)
                customLayout?.layoutParams = dialogParam
            }

            diyView?.run {
                contentLayout?.removeAllViews()
                contentLayout?.addView(diyView)
                if (diyViewMarginHorizontal != 0 || diyViewMarginTop != 0 || diyViewMarginBottom != 0) {
                    val diyViewParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    diyViewParam.setMargins(diyViewMarginHorizontal, diyViewMarginTop, diyViewMarginHorizontal, diyViewMarginBottom)
                    contentLayout?.layoutParams = diyViewParam
                }
            }

            // 绑定视图
            dialogView?.run { setContentView(this) }

            dismissListener?.run {
                mDialog?.setOnDismissListener { dismissListener?.doDismiss() }
            }

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
                    Log.e("VDialog-Error", "${e.message}")
                }

            }
        }
    }

}
