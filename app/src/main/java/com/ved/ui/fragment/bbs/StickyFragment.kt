package com.ved.ui.fragment.bbs

import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ved.R
import com.ved.entity.adapter.StickyItemEntity
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_list_sticky.*
import kotlinx.android.synthetic.main.item_sticky.view.*
import kotlinx.android.synthetic.main.item_sticky_head.view.*

class StickyFragment : BaseFragment() {

    private var mStickyList = ArrayList<StickyItemEntity>()

    override fun loadView(): Int {
        return R.layout.fragment_list_sticky
    }

    override fun init() {
        initTitle("列表吸顶")

        rec_sticky?.adapter = stickyAdapter

        for (i in 1..60) {
            val stickyItem = StickyItemEntity(name = "列表Item$i", headFlag = i == 6 || i == 28, entityType = if (i == 6 || i == 28) 1 else 0)
            mStickyList.add(stickyItem)
        }

        stickyAdapter.setList(mStickyList)

        val baseUseArea = "吸顶场景"
        val baseUseRole = "布局文件直接使用控件，自定义属性。代码使用重置计时器方法和取消倒计时。"
        val baseInteract = "倒计时开始后，每秒都会触发倒计时监听，倒计时结束后触发计时结束监听。"
        val baseStyle = "可自定义文字样式"

        initContent(baseUseArea, baseUseRole, baseInteract, baseStyle)
    }

    override fun doExecute() {

    }

    private val stickyAdapter = object : BaseSectionQuickAdapter<StickyItemEntity, BaseViewHolder>(R.layout.item_sticky_head, mStickyList) {
        init {
            addItemType(0, R.layout.item_sticky)
            addItemType(StickyItemEntity.ITEM_TYPE_SPAN, R.layout.item_sticky_head)
        }

        override fun convert(holder: BaseViewHolder, item: StickyItemEntity) {
            holder.itemView.tv_list_sticky?.text = "${item.name}${holder.layoutPosition + 1}"
        }

        override fun convertHeader(helper: BaseViewHolder, item: StickyItemEntity) {
            helper.itemView.tv_list_header_sticky?.text = "${item.name}${helper.layoutPosition + 1}"
        }
    }

}