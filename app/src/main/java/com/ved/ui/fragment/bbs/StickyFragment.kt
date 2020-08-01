package com.ved.ui.fragment.bbs

import android.graphics.Color
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ved.R
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_list_sticky.*
import kotlinx.android.synthetic.main.fragment_timer_down.*
import kotlinx.android.synthetic.main.item_sticky.view.*

class StickyFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_list_sticky
    }

    override fun init() {
        initTitle("列表吸顶")

        rec_sticky?.adapter = stickyAdapter

        val strList = ArrayList<StickyItem>()
        for (i in 1..40) {
            strList.add(StickyItem().apply { name = "列表Item" })
        }

        stickyAdapter.replaceData(strList)

        val baseUseArea = "吸顶场景"
        val baseUseRole = "布局文件直接使用控件，自定义属性。代码使用重置计时器方法和取消倒计时。"
        val baseInteract = "倒计时开始后，每秒都会触发倒计时监听，倒计时结束后触发计时结束监听。"
        val baseStyle = "可自定义文字样式"

        initContent(baseUseArea, baseUseRole, baseInteract, baseStyle)
    }

    override fun doExecute() {

        activity_timer_countdown_default?.resetTimer((1000 * 60 * 60 * 12.34).toLong(), 1000L)
        activity_timer_countdown_text?.setTimerBackground(Color.RED)

        activity_timer_countdown_text?.setTimerTextColor(Color.BLACK)
        activity_timer_countdown_text?.setTimerColonColor(Color.GREEN)
        activity_timer_countdown_text?.resetTimer((1000 * 60 * 60 * 56.78).toLong(), 1000L)
    }

    private val stickyAdapter = object : BaseQuickAdapter<StickyItem, BaseViewHolder>(R.layout.item_sticky) {
        override fun convert(helper: BaseViewHolder, item: StickyItem?) {
            helper.itemView.tv_list_sticky?.text = "${item?.name}${helper.layoutPosition + 1}"
        }
    }

    class StickyItem {
        var name: String? = null
    }

}