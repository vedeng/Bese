package com.ved.ui.fragment.component

import com.ved.R
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_input.*

class InputViewFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_input
    }

    override fun init() {

        initTitle("输入框")

        val baseUseArea = "需要用户输入的场景"
        val baseUseRole = "布局文件直接使用控件，自定义属性"
        val baseInteract = "控件默认是原生输入控件，可通过属性自定义左右文字和图标。"
        val baseStyle = "可自定义左侧提示文字样式、输入框样式、右侧清除或可见性图标、下划线显隐"
        initContent(baseUseArea, baseUseRole, baseInteract, baseStyle)
    }

    override fun doExecute() {
//        input_left_tip?.setTipText("左侧")
        input_left_tip?.setTipText("左", context?.getDrawable(R.mipmap.chat))
    }

}