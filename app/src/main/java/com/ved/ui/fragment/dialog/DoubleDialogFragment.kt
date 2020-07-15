package com.ved.ui.fragment.dialog

import com.bese.widget.dialog.XDialog
import com.ved.R
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_dialog_double.*

class DoubleDialogFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_dialog_double
    }

    override fun init() {
        initTitle("双按钮弹窗")
    }

    override fun doExecute() {
        tv_dialog_timer_title?.setOnClickListener {
            XDialog(baseContext)
                .setTitle("领取成功！${XDialog.TIMER_FLAG}秒后自动关闭。")
                .setMessage("感谢您的参与，")
                .setTimer(5200,1000, XDialog.TIMER_TYPE_TITLE)
                .setCancelText("关闭")
                .setEnterText("去使用")
                .build()
        }

        tv_dialog_timer_message?.setOnClickListener {
            XDialog(baseContext)
                .setTitle("领取成功！")
                .setMessage("感谢您的参与，${XDialog.TIMER_FLAG}秒后自动关闭。")
                .setTimer(5200,1000, XDialog.TIMER_TYPE_MESSAGE)
                .setCancelText("关闭")
                .setEnterText("去使用")
                .build()
        }

        tv_dialog_timer_cancel?.setOnClickListener {
            XDialog(baseContext)
                .setTitle("领取成功！")
                .setMessage("感谢您的参与，")
                .setTimer(5200,1000, XDialog.TIMER_TYPE_CANCEL)
                .setCancelText("${XDialog.TIMER_FLAG}秒后关闭")
                .setEnterText("去使用")
                .build()
        }

        tv_dialog_timer_enter?.setOnClickListener {
            XDialog(baseContext)
                .setTitle("领取成功！")
                .setMessage("感谢您的参与，")
                .setTimer(5200,1000, XDialog.TIMER_TYPE_ENTER)
                .setCancelText("关闭")
                .setEnterText("去使用 (${XDialog.TIMER_FLAG})")
                .build()
        }
    }

}