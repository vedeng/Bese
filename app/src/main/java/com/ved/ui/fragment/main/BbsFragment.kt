package com.ved.ui.fragment.main

import com.ved.R
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_bbs.*

class BbsFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_bbs
    }

    override fun init() {
        tv_page_sticky?.setOnClickListener {                      // 列表吸顶效果
            navigate(R.id.action_mainFragment_to_stickyFragment)
        }
        tv_page_stagger?.setOnClickListener {                  // 流式布局效果
            navigate(R.id.action_mainFragment_to_staggerFragment)
        }
        tv_page_coordinator?.setOnClickListener {          // 协调布局效果
            navigate(R.id.action_mainFragment_to_coordinatorFragment)
        }
    }

    override fun doExecute() {

    }

}