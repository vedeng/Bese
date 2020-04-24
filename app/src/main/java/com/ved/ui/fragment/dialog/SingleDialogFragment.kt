package com.ved.ui.fragment.dialog

import com.ved.R
import com.ved.ui.base.BaseFragment

class SingleDialogFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_dialog_single
    }

    override fun init() {
        initTitle("单按钮弹窗")
    }

    override fun doExecute() {
    }

    override fun clickRight() {
        navigateTo(R.id.dialogListFragment)
    }

}