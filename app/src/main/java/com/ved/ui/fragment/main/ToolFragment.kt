package com.ved.ui.fragment.main

import com.ved.R
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_tool.*

class ToolFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_tool
    }

    override fun init() {
//        tv_add?.setOnClickListener { navigate(R.id.action_toolFragment_to_locationFragment) }
    }

    override fun doExecute() {
    }

}