package com.ved.ui.fragment.main

import android.content.Intent
import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ved.R
import com.ved.entity.bean.MainListEntity
import com.ved.ui.DetailCompressActivity
import com.ved.ui.DetailImageViewActivity
import com.ved.ui.DetailPicPickerActivity
import com.ved.ui.EmptyPageActivity
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_vd_text.view.*
import kotlinx.android.synthetic.main.item_vd_title.view.*

class HomeFragment : BaseFragment() {

    private var mainList: ArrayList<MainListEntity>? = null

    override fun loadView(): Int {
        return R.layout.fragment_home
    }

    override fun init() {
        rec_main?.adapter = mainAdapter
    }

    override fun doExecute() {
        getMainListFlow()


        mainList?.run { mainAdapter.setList(this) }

    }

    private var mainAdapter = object : BaseMultiItemQuickAdapter<MainListEntity, BaseViewHolder>(null) {
        init {
            addItemType(MainListEntity.ITEM_TITLE, R.layout.item_vd_title)
            addItemType(MainListEntity.ITEM_CONTENT, R.layout.item_vd_text)
        }
        override fun convert(holder: BaseViewHolder, item: MainListEntity) {
            item.run {
                when (itemType) {
                    MainListEntity.ITEM_TITLE -> {
                        holder.itemView.tv_title?.text = content
                    }
                    MainListEntity.ITEM_CONTENT -> {
                        holder.itemView.tv_text?.text = content
                        holder.itemView.item_text?.tag = "home_rec_${holder.adapterPosition + 1}_$content"
                        holder.itemView.item_text?.setOnClickListener {
                            jumpListener.doJump(content)
                            Log.e("Home列表ID====", "${holder.itemView.id}    +    ${holder.itemView.item_text.id}")
                        }
                    }
                    else -> { }
                }
            }
        }
    }

    private var jumpListener = object : JumpEventListener {
        override fun doJump(tag: String?) {
            when(tag) {
                arr1[0] -> navigate(R.id.action_mainFragment_to_buttonFragment)
                arr1[1] -> navigate(R.id.action_mainFragment_to_richTextFragment)
                arr1[2] -> navigate(R.id.action_mainFragment_to_fontIconFragment)
                arr1[3] -> navigate(R.id.action_mainFragment_to_badgeFragment)
                arr1[4] -> navigate(R.id.action_mainFragment_to_lottieFragment)
                arr2[0] -> navigate(R.id.action_mainFragment_to_inputViewFragment)
                arr2[1] -> navigate(R.id.action_mainFragment_to_numEditorFragment)
                arr2[2] -> navigate(R.id.action_mainFragment_to_bannerFragment)
                arr2[3] -> navigate(R.id.action_mainFragment_to_timerDownFragment)
                arr3[0] -> navigate(R.id.action_mainFragment_to_dialogListFragment)
                arr3[1] -> navigate(R.id.action_mainFragment_to_bottomDialogFragment)
                arr3[2] -> navigate(R.id.action_mainFragment_to_topDialogFragment)
                arr3[3] -> navigate(R.id.action_mainFragment_to_popupFragment)
                arr3[4] -> navigate(R.id.action_mainFragment_to_toastListFragment)
                arr4[0] -> navigate(R.id.action_mainFragment_to_locationFragment)//ToastUtils.showShort("开关~")
                arr4[1] -> navigate(R.id.action_mainFragment_to_selectItemFragment)
                arr4[2] -> ToastUtils.showShort("选项卡~")
                arr4[3] -> startActivity(Intent(baseContext, DetailPicPickerActivity::class.java))
                arr4[4] -> navigate(R.id.action_mainFragment_to_selectAddressFragment)
                arr4[5] -> ToastUtils.showShort("选择时间~")
                arr4[6] -> ToastUtils.showShort("多级联动~")
                arr5[0] -> startActivity(Intent(baseContext, DetailImageViewActivity::class.java))
                arr5[1] -> startActivity(Intent(baseContext, DetailCompressActivity::class.java))
                arr5[2] -> ToastUtils.showShort("图片上传~")
                arr5[3] -> startActivity(Intent(baseContext, EmptyPageActivity::class.java))
            }
        }
    }

    private fun getMainListFlow() {
        map[arr[0]] = arr1
        map[arr[1]] = arr2
        map[arr[2]] = arr3
        map[arr[3]] = arr4
        map[arr[4]] = arr5
        mainList = ArrayList()
        map.forEach {
            mainList?.add(MainListEntity(true, it.key))
            it.value.forEach { v ->
                mainList?.add(MainListEntity(false, v))
            }
        }
    }

    companion object {
        var arr = arrayOf("单元控件", "多元组件", "弹窗", "选择模块", "功能模块")
        var arr1 = arrayOf("按钮", "富文本", "字体图标", "徽标", "Lottie动画")
        var arr2 = arrayOf("输入框", "步进器", "Banner", "倒计时")
        var arr3 = arrayOf("弹窗", "底部弹窗", "顶部弹窗", "气泡弹窗", "吐司")
        var arr4 = arrayOf("开关", "单选复选框", "选项卡", "图片选择", "地址选择", "时间选择", "多级联动")
        var arr5 = arrayOf("图片展示", "图片压缩", "图片上传", "空页面")
        var map = LinkedHashMap<String, Array<String>>()
    }

    interface JumpEventListener {
        fun doJump(tag: String?)
    }

}