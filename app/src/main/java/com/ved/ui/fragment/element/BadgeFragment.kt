package com.ved.ui.fragment.element

import android.view.Gravity
import com.bese.widget.badge.BadgeView
import com.ved.R
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_badge.*

class BadgeFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_badge
    }

    override fun init() {
        initTitle("徽标")

        val baseUseArea = "●单个文本框使用多种样式的场景"
        val baseUseRole = "●直接引用TextButton，定义属性：\n" +
                "●圆角值\n" +
                "●边框粗细（默认0无边框）\n" +
                "●边框颜色\n" +
                "●背景颜色\n" +
                "●按下背景色\n"
        val baseInteract = "●控件默认含有触摸事件。" +
                "●如果定义按下背景色，按下时颜色改变；如果没有设置，按下无感知（有点击事件）"
        val baseStyle = "●可定义圆角大小，圆角非常大时变成粗线形式" +
                "●可定义是否有边框，以及各种颜色"
        initContent(baseUseArea, baseUseRole, baseInteract, baseStyle)

    }

    override fun doExecute() {

        BadgeView(requireContext())
            .setBadgeText("5")
            .setExactMode(true)
            .setBadgeGravity(Gravity.TOP or Gravity.END)
            .bind(tv_1)

        BadgeView(requireContext())
            .setBadgeNumber(8)
            .setBadgeGravity(Gravity.TOP or Gravity.END)
            .bind(tv_2)

        BadgeView(requireContext())
            .setBadgeNumber(55)
            .setBadgeBackground(baseContext?.getDrawable(R.drawable.shape_cursor_style))
            .setBadgeGravity(Gravity.TOP or Gravity.END)
            .bind(tv_3)

        BadgeView(requireContext())
            .setBadgeText("")
            .setBadgeGravity(Gravity.TOP or Gravity.END)
            .bind(tv_4)

    }

}