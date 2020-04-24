package com.ved.util.fragmentnav

import androidx.fragment.app.Fragment

/**
 * < Fragment管理接口 >
 */
interface FragmentNavigatorAdapter {
    /**
     * 子类创建Fragment的方法
     * @param position 位置
     * @return Fragment
     */
    fun onCreateFragment(position: Int): Fragment?

    /**
     * 获取Fragment的标签
     * @param position 位置
     * @return 标签
     */
    fun getTag(position: Int): String?

    /**
     * Fragment的个数
     * @return 个数
     */
    val count: Int
}