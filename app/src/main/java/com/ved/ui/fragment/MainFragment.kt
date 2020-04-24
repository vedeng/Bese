package com.ved.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ved.R
import com.ved.ui.base.BaseFragment
import com.ved.util.fragmentnav.BottomNavView
import com.ved.util.fragmentnav.FragmentNavigator
import com.ved.util.fragmentnav.FragmentNavigatorAdapter
import com.ved.util.fragmentnav.NavigatorAdapter
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseFragment(), BottomNavView.OnNavClickListener {

    /** Fragment管理 */
    private var mNavigator: FragmentNavigator? = null
    private var mNavigatorAdapter: FragmentNavigatorAdapter? = null

    private var currentTabPosition = DEFAULT_POSITION

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun loadView(): Int {
        return R.layout.fragment_main
    }

    override fun init() {
        bottom_nav?.setOnNavClickListener(this)
        mNavigatorAdapter = NavigatorAdapter()
        mNavigator = FragmentNavigator(activity?.supportFragmentManager, mNavigatorAdapter, R.id.main_container)
        mNavigator?.setDefaultPosition(DEFAULT_POSITION, true)
    }

    override fun doExecute() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("MainFragment====", "销毁")
    }

    override fun onNavClick(position: Int, view: View?) {
        setSelectTab(position)
    }

    private fun setSelectTab(position: Int) {
        mNavigator?.showFragment(position, allowingStateLoss = true)
        bottom_nav?.select(position)
        currentTabPosition = position
        LAST_POSITION = position
    }

    companion object {
        private const val DEFAULT_POSITION = 0
        var LAST_POSITION = -1
    }

}