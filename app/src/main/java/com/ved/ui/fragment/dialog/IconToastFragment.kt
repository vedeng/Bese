package com.ved.ui.fragment.dialog

import com.ved.R
import com.ved.ui.base.BaseFragment

class IconToastFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_toast_with_icon
    }

    override fun init() {
        initTitle("带图标吐司")
    }

    override fun doExecute() {
    }

}