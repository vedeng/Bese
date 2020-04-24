package com.ved.ui

import com.ved.R
import com.ved.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_empty_page.*
import kotlinx.android.synthetic.main.base_title_bar.*

class EmptyPageActivity : BaseActivity() {

    override fun loadView() : Int {
        return R.layout.activity_empty_page
    }

    override fun init() {
        initTitle("空页面")

        val baseUseArea = "需要展示加载动画、空页面、错误页面的场景。"
        val baseUseRole = "布局文件中直接使用LoadContainer，展示时调用其show系列方法。"
        val baseInteract = "同一时刻只能显示一种状态"
        val baseStyle = "可自定义界面中所有元素"
        initContent(baseUseArea, baseUseRole, baseInteract, baseStyle)
    }

    override fun doExecute() {

        activity_empty_page_loading?.showLoading()
        activity_empty_page_empty?.showEmpty()
        activity_empty_page_error?.showError()

        base_back?.setOnClickListener { finish() }
    }

}
