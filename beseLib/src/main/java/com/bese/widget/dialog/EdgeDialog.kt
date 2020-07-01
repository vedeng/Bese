package com.bese.widget.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bese.R

/**
 * <顶部或底部出现的边缘弹窗>
 */
class EdgeDialog(private var isTop: Boolean = false) : DialogFragment() {

    /** 自定义布局 */
    private var diyView: View? = null
    /** 触摸阴影弹窗消失 */
    private var dismissTouchDim = true
    /** 消失事件 */
    private var dialogListener: DialogListener? = null

    fun setEdgeTop(isTop: Boolean) : EdgeDialog {
        this.isTop = isTop
        return this
    }

    fun setEdgeView(view: View?) : EdgeDialog {
        diyView = view
        return this
    }

    fun setDismissListener(listener: DialogListener?) : EdgeDialog {
        dialogListener = listener
        return this
    }

    fun setDismissOnTouchDim(touchDimHide: Boolean) : EdgeDialog {
        dismissTouchDim = touchDimHide
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireActivity(), if (isTop) R.style.Dialog_Fullscreen_Top else R.style.Dialog_Fullscreen_Bottom)
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_top_or_bottom, dialog?.window?.findViewById(android.R.id.content), false)
        val edgeView = view.findViewById<LinearLayout>(R.id.layout_edge)
        initView(edgeView)
        dialogBuilder.setView(view)
        // 返回自定义布局的Dialog
        return dialogBuilder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.run {
            setCanceledOnTouchOutside(dismissTouchDim)
            window?.run {
                decorView.setPadding(0, 0, 0, 0)
                val attributes = window?.attributes
                attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
                attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT
                attributes?.gravity = if (isTop) Gravity.TOP else Gravity.BOTTOM
                window?.attributes = attributes
            }
        }
    }

    private fun initView(view: LinearLayout?) {
        view?.run {
            diyView?.let {
                this.removeAllViews()
                this.addView(it)
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        diyView = null
        dialogListener?.doDismiss()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            Log.e("EdgeDialog-Error", "${e.message}")
        }
    }

}
