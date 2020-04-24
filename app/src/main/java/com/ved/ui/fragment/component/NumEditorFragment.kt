package com.ved.ui.fragment.component

import android.widget.Toast
import com.bese.widget.numeditor.NumEditor
import com.ved.R
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_num_editor.*

class NumEditorFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_num_editor
    }

    override fun init() {
        initTitle("步进条")

        val baseUseArea = "●需要手动调整数量时使用，如购物车商品数量增减，详情页商品簇，订单确认页数量修改"

        val baseUseRole = "●数量至少为1个，可定义单人购买数量限制，也包含最大可用数量限制\n" +
                "●可以通过加减号增减数量，下限数量时减号按钮置灰不可点，上限数量时加号按钮置灰不可点，在此区间的数量加减都可点\n" +
                "●弹出键盘修改数量时，允许数量为空，但不可从0开始输入，输入长度超限时自动变为最大数量"

        val baseInteract = "●用户点击加减号，数量增减1，用户点击数字区域，弹出键盘输入，收起键盘时，检查数量是否符合要求"

        val baseStyle = "●按钮区域形如正方形，数字区域宽度约等于按钮宽度的两倍\n" +
                " - 数字底色白色，按钮底色 #f5f7fa，边框线#ced2d9\n" +
                "●尺寸可修改\n" +
                "●按钮点击区域为整个按钮，数字点击区域有3dp的内边距"

        initContent(baseUseArea, baseUseRole, baseInteract, baseStyle)
    }

    override fun doExecute() {

        editor_num?.setNumMaxLimit(6666)
        editor_num?.setNum(1)
        editor_num?.setOnWarnListener(object : NumEditor.OnWarnListener {
            override fun onWarningForInventory(inventory: Int) {
                Toast.makeText(baseContext, "库存达到上限: $inventory", Toast.LENGTH_SHORT).show()
            }
            override fun onWarningForBuyMax(max: Int) {
                Toast.makeText(baseContext, "数量上限: $max", Toast.LENGTH_SHORT).show()
            }
        })
    }

}