package com.ved.ui.fragment.dialog

import com.blankj.utilcode.util.ToastUtils
import com.ved.R
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_toast_list.*

class ToastListFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_toast_list
    }

    override fun init() {
        initTitle("吐司场景")


        ToastUtils.showShort("Init")
    }

    override fun doExecute() {
        tv_toast_system_button?.setOnClickListener { navigate(R.id.action_toastListFragment_to_systemToastFragment) }
        tv_toast_icon_button?.setOnClickListener { navigate(R.id.action_toastListFragment_to_iconToastFragment) }
    }

}