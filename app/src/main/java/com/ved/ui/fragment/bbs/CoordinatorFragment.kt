package com.ved.ui.fragment.bbs

import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.appbar.AppBarLayout
import com.ved.R
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_coordinator.*
import kotlinx.android.synthetic.main.fragment_list_stagger.*
import kotlinx.android.synthetic.main.item_sticky.view.*
import org.jetbrains.anko.sdk27.coroutines.onAttachStateChangeListener
import kotlin.math.abs


class CoordinatorFragment : BaseFragment() {

    var listTitle: ArrayList<String> = ArrayList()

    override fun loadView(): Int {
        return R.layout.fragment_coordinator
    }

    override fun init() {
        initTitle("流式布局")

        rec_stagger?.adapter = stickyAdapter

        val baseUseArea = "流式布局场景"
        val baseUseRole = "布局文件直接使用控件，自定义属性。代码使用重置计时器方法和取消倒计时。"
        val baseInteract = "倒计时开始后，每秒都会触发倒计时监听，倒计时结束后触发计时结束监听。"
        val baseStyle = "可自定义文字样式"

        initContent(baseUseArea, baseUseRole, baseInteract, baseStyle)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun doExecute() {
        listTitle.add("标题1") //标题
        listTitle.add("标题二") //标题
        tab_main?.run {
            listTitle.forEach { addTab(newTab().setText(it)) }
            setupWithViewPager(view_pager)
        }

        view_pager?.adapter = object : FragmentPagerAdapter(requireActivity().supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            private var frames = arrayOf(StickyFragment(), StaggerFragment())
            override fun getPageTitle(position: Int): CharSequence? { return listTitle[position] }
            override fun getItem(position: Int): Fragment { return frames[position] }
            override fun getCount(): Int { return frames.size }
        }

        layout_app_bar?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val maxOffset = appBarLayout.totalScrollRange
            val offset = abs(verticalOffset)
            Log.e("协调布局滚动距离===", "$verticalOffset  -  $maxOffset")
            if (offset <= layout_banner_ad?.height ?: 0) {
                tv_out_bar?.text = "SearchBar-level1"
            } else {
                tv_out_bar?.text = "SearchBar-level2"
            }
        })
    }

    private val stickyAdapter = object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_sticky) {
        override fun convert(holder: BaseViewHolder, item: String) {
            holder.itemView.tv_list_sticky?.text = item
        }
    }

}