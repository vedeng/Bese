package com.ved.ui.fragment.dialog

import com.ved.R
import com.ved.ui.base.BaseFragment

class DoubleDialogFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_dialog_double
    }

    override fun init() {
        initTitle("双按钮弹窗")
    }

    override fun doExecute() {
    }

}