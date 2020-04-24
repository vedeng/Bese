package com.bese.widget.dialog

import android.app.Dialog

/**
 * Dialog监听接口
 */
interface DialogListener {
    /** 选择 */
    fun doEnter(view: Dialog?)

    /** 取消事件 */
    fun doCancel(view: Dialog?) { }

    /** 取消事件 */
    fun doDismiss() { }
}
