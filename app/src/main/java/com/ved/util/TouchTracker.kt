package com.ved.util

import android.view.View
import android.view.ViewGroup
import java.lang.reflect.Field

open class TouchTracker() {

    /**
     * a reflect field to find mFirstTouchTarget from ViewGroup
     */
    private var sTouchTargetField: Field? = null

    /**
     * a reflect field to find child from TouchTarget
     */
    private var sTouchTargetChildField: Field? = null

    /**
     * the root view to find focus touch view from,
     * general a give a DecorView is a better way
     */
    private var mRootView: ViewGroup? = null

    constructor(rootView: ViewGroup?) : this() {
        mRootView = rootView
    }

    /**
     * reflect to find the TouchTarget child view,null if not found
     */
    open fun findTargetView(parent: ViewGroup): View? {
        try {
            val target = sTouchTargetField?.get(parent)
            if (target != null) {
                val view: Any? = sTouchTargetChildField?.get(target)
                if (view is View) {
                    return view
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * find the target view who is interest in the touch event. null if not find
     */
    open fun findTargetView(): View? {
        var nextTarget: View?
        var target: View? = null
        if (ensureTargetField() && mRootView != null) {
            nextTarget = findTargetView(mRootView!!)
            do {
                target = nextTarget
                nextTarget = null
                if (target is ViewGroup) {
                    nextTarget = findTargetView(target)
                }
            } while (nextTarget != null)
        }
        return target
    }

    /**
     * ensure the reflect field is available
     *
     * @return true if both field is not null.
     */
    open fun ensureTargetField(): Boolean {
        if (sTouchTargetField == null) {
            try {
                val viewClass = Class.forName("android.view.ViewGroup")
                if (viewClass != null) {
                    sTouchTargetField = viewClass.getDeclaredField("mFirstTouchTarget")
                    sTouchTargetField?.isAccessible = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                if (sTouchTargetField != null) {
                    sTouchTargetChildField = sTouchTargetField?.type?.getDeclaredField("child")
                    sTouchTargetChildField?.isAccessible = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return sTouchTargetField != null && sTouchTargetChildField != null
    }

}