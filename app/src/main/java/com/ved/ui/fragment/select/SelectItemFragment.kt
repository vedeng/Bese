package com.ved.ui.fragment.select

import com.ved.R
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_select_item.*
import kotlin.random.Random

/**
 * Author: Endless
 * Date: 2020/5/19
 * Desc:
 */
class SelectItemFragment : BaseFragment() {
    override fun loadView(): Int {
        return R.layout.fragment_select_item
    }

    override fun init() {
        initTitle("单选复选")

        fragment_select_item_sl_01?.run {
            resetChildren(R.layout.item_select_label, generateLabels())
        }

        fragment_select_item_sl_02?.run {
            resetChildren(R.layout.item_select_label, generateLabels())
        }

        val baseUseArea = "●单选复选场景使用，可自动换行，改变颜色。"

        val baseUseRole = "●可设置单选和多选\n" +
                "●可设置最大、最小选中数量\n"

        val baseInteract = "●点你就懂了"

        val baseStyle = "●未选中为灰底黑字，选中为蓝色描边蓝字\n" +
                "●字体大小为16sp, 标签横向间距为16dp, 纵向间距为10dp\n" +
                "●标签间距为10dp\n" +
                "●尺寸可修改，选中样式可修改\n"

        initContent(baseUseArea, baseUseRole, baseInteract, baseStyle)
    }

    override fun doExecute() {
    }

    private fun generateLabels(): List<String> {
        val list = arrayListOf<String>()
        for (index in 0..Random.nextInt(5, 20)) {
            list.add("标签$index")
        }
        return list
    }
}