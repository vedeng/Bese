package com.ved.util.fragmentnav

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * <Fragment 使用>
 */
class FragmentNavigator(
    private val mFragmentManager: FragmentManager?,
    private val navigatorAdapter: FragmentNavigatorAdapter?,
    @field:IdRes @param:IdRes private val mContainerViewId: Int
) {

    companion object {
        private const val EXTRA_CURRENT_POSITION = "extra_current_position"
    }

    /**
     * @return current showing fragment's position
     */
    var currentPosition = -1
        private set

    private var mDefaultPosition = 0

    fun restore(savedInstanceState: Bundle? = null) {
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(EXTRA_CURRENT_POSITION, mDefaultPosition)
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(EXTRA_CURRENT_POSITION, currentPosition)
    }

    /**
     * Show fragment at given position
     *
     * @param position fragment position
     * @param reset true if fragment in given position need reset otherwise false
     * @param allowingStateLoss true if allowing state loss otherwise false
     */
    @JvmOverloads fun showFragment(position: Int, reset: Boolean = false, allowingStateLoss: Boolean = false) {
        currentPosition = position
        val transaction = mFragmentManager?.beginTransaction()
        val count = navigatorAdapter?.count ?: 0
        for (i in 0 until count) {
            if (position == i) {
                if (reset) {
                    remove(position, transaction)
                    add(position, transaction)
                } else {
                    show(i, transaction)
                }
            } else {
                hide(i, transaction)
            }
        }
        if (allowingStateLoss) {
            transaction?.commitAllowingStateLoss()
        } else {
            transaction?.commit()
        }
    }

    /**
     * reset all the fragment and show given position fragment
     *
     * @param position fragment position
     * @param allowingStateLoss true if allowing state loss otherwise false
     */
    @JvmOverloads fun resetFragments(position: Int = currentPosition, allowingStateLoss: Boolean = false) {
        currentPosition = position
        val transaction = mFragmentManager?.beginTransaction()
        removeAll(transaction)
        add(position, transaction)
        if (allowingStateLoss) {
            transaction?.commitAllowingStateLoss()
        } else {
            transaction?.commit()
        }
    }

    /**
     * remove all fragment in the [FragmentManager]
     *
     * @param allowingStateLoss true if allowing state loss otherwise false
     */
    @JvmOverloads fun removeAllFragment(allowingStateLoss: Boolean = false) {
        val transaction = mFragmentManager?.beginTransaction()
        removeAll(transaction)
        if (allowingStateLoss) {
            transaction?.commitAllowingStateLoss()
        } else {
            transaction?.commit()
        }
    }

    /**
     * Get the fragment has been added in the given position. Return null if the fragment
     * hasn't been added in [FragmentManager] or has been removed already.
     *
     * @param position position of fragment in [FragmentNavigatorAdapter.onCreateFragment]}
     * and [FragmentNavigatorAdapter.getTag]
     * @return The fragment if found or null otherwise.
     */
    fun getFragment(position: Int = currentPosition): Fragment? {
        val tag = navigatorAdapter?.getTag(position)
        return mFragmentManager?.findFragmentByTag(tag)
    }

    private fun show(position: Int, transaction: FragmentTransaction?) {
        val tag = navigatorAdapter?.getTag(position)
        val fragment = mFragmentManager?.findFragmentByTag(tag)
        if (fragment == null) {
            add(position, transaction)
        } else {
            transaction?.show(fragment)
        }
    }

    private fun hide(position: Int, transaction: FragmentTransaction?) {
        val tag = navigatorAdapter?.getTag(position)
        val fragment = mFragmentManager?.findFragmentByTag(tag)
        if (fragment != null && !fragment.isHidden) {
            transaction?.hide(fragment)
        }
    }

    private fun add(position: Int, transaction: FragmentTransaction?) {
        val fragment = navigatorAdapter?.onCreateFragment(position)
        if (fragment != null) {
            val tag = navigatorAdapter?.getTag(position)
            transaction?.add(mContainerViewId, fragment, tag)
        }
    }

    private fun removeAll(transaction: FragmentTransaction?) {
        val count = navigatorAdapter?.count ?: 0
        for (i in 0 until count) {
            remove(i, transaction)
        }
    }

    private fun remove(position: Int, transaction: FragmentTransaction?) {
        val tag = navigatorAdapter?.getTag(position)
        val fragment = mFragmentManager?.findFragmentByTag(tag)
        if (fragment != null) {
            transaction?.remove(fragment)
        }
    }

    fun setDefaultPosition(defaultPosition: Int, selectDefault: Boolean = false) {
        mDefaultPosition = defaultPosition
        if (currentPosition == -1) {
            currentPosition = defaultPosition
        }
        if (selectDefault) {
            showFragment(defaultPosition)
        }
    }

}