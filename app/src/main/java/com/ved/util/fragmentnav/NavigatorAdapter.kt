package com.ved.util.fragmentnav

import androidx.fragment.app.Fragment
import com.ved.ui.fragment.main.*

/**
 * <Fragment管理适配>
 */
class NavigatorAdapter : FragmentNavigatorAdapter {

    companion object {
        private val TAB = arrayOf("HomeTab", "ToolTab", "FeedbackTab", "BbsTab", "AboutTab")
    }
    var fragments = arrayListOf(
        HomeFragment(),
        ToolFragment(),
        FeedbackFragment(),
        BbsFragment(),
        AboutFragment()
    )

    override fun onCreateFragment(position: Int): Fragment? {
        return when (position) {
            0 -> fragments[0]
            1 -> fragments[1]
            2 -> fragments[2]
            3 -> fragments[3]
            4 -> fragments[4]
            else -> null
        }
    }

    override fun getTag(position: Int): String? {
        return TAB[position]
    }

    override val count: Int
        get() = TAB.size

}