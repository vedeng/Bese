package com.ved.ui.fragment.dialog

import com.ved.R
import com.ved.ui.base.BaseFragment

class SystemToastFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_toast_system
    }

    override fun init() {
        initTitle("系统吐司")
    }

    override fun doExecute() {
    }

}