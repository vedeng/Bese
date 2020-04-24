package com.ved.ui.fragment.dialog

import com.ved.R
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_dialog_list.*

class DialogListFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_dialog_list
    }

    override fun init() {
        initTitle("选择弹窗场景")
    }

    override fun doExecute() {
        tv_dialog_single_button?.setOnClickListener { navigate(R.id.action_dialogListFragment_to_singleDialogFragment) }
        tv_dialog_double_button?.setOnClickListener { navigate(R.id.action_dialogListFragment_to_doubleDialogFragment) }
    }

}
