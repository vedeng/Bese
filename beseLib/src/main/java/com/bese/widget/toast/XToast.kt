package com.bese.widget.toast

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.bese.R
import java.util.*

/**
 * <超级 Toast>
 */
class XToast(val context: Context) {

    /** 当前是否已经显示 */
    @Volatile var isShow = false
    /** 显示时长 */
    private var mDuration = 1500L
    /** 获取布局 */
    private var view: View? = null
    /** 获取 WindowManager 参数集 */
    private var windowParams: WindowManager.LayoutParams? = null
    private var windowManager: WindowManager? = null

    init {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        windowParams = WindowManager.LayoutParams()
        // 配置一些默认的参数
        windowParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        windowParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        windowParams?.format = PixelFormat.TRANSLUCENT
        windowParams?.windowAnimations = android.R.style.Animation_Toast
        windowParams?.gravity = Gravity.CENTER
    }

    /** 添加标志位 */
    fun addFlags(flag: Int): XToast {
        windowParams?.run { flags = flags or flag }
        return this
    }

    /** 设置标志位  */
    fun setFlags(flags: Int): XToast {
        windowParams?.flags = flags
        return this
    }

    /** 设置显示的类型 */
    fun setType(type: Int): XToast {
        windowParams?.type = type
        return this
    }

    /** 设置动画样式 */
    fun setAnimStyle(resId: Int): XToast {
        windowParams?.windowAnimations = resId
        return this
    }

    /** 设置宽度 */
    fun setWidth(width: Int): XToast {
        windowParams?.width = width
        return this
    }

    /** 设置高度 */
    fun setHeight(height: Int): XToast {
        windowParams?.height = height
        return this
    }

    /** 限定显示时长 */
    fun setDuration(duration: Long): XToast {
        mDuration = duration
        return this
    }

    /** 设置重心 */
    fun setGravity(gravity: Int): XToast {
        windowParams?.gravity = gravity
        return this
    }

    /** 设置 X 轴偏移量 */
    fun setXOffset(x: Int): XToast {
        windowParams?.x = x
        return this
    }

    /** 设置 Y 轴偏移量 */
    fun setYOffset(y: Int): XToast {
        windowParams?.y = y
        return this
    }

    /** 设置 WindowManager 参数集 */
    fun setWindowParams(params: WindowManager.LayoutParams?): XToast {
        windowParams = params
        return this
    }

    /**
     * 显示
     */
    fun show(): XToast {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.toast_hint_noicon, null)
        }
        if (isShow) {
            cancel()
        }
        try {       // 如果这个 View 对象被重复添加到 WindowManager 则会抛出异常
            windowManager?.addView(view, windowParams)
            // 当前已经显示
            isShow = true
            // 如果当前限定了显示时长
            if (mDuration != 0L) {
                Timer().schedule(object : TimerTask() {
                    override fun run() { if (isShow) this@XToast.cancel() }
                }, mDuration)
            }
        } catch (ignored: Exception) {
            Log.e("XToast-Error", "${ignored.message}")
        }
        return this
    }

    /**
     * 取消
     */
    fun cancel(): XToast {
        if (isShow) {
            try {       // 如果当前 WindowManager 没有附加这个 View 则会抛出异常
                windowManager?.removeView(view)
            } catch (ignored: NullPointerException) {
                Log.e("XToast maybe destroyed", "=" + ignored.message)
            } catch (ignored: IllegalArgumentException) {
                Log.e("XToast maybe destroyed", "=" + ignored.message)
            }
            // 当前没有显示
            isShow = false
        }
        return this
    }

    /** 设置布局 */
    fun setView(layoutId: Int): XToast {
        return setView(LayoutInflater.from(context).inflate(layoutId, null))
    }

    fun setView(view: View?): XToast {
        cancel()
        this.view = view
        return this
    }

    /**
     * 根据 ViewId 获取 View
     */
    fun <V : View?> findViewById(id: Int): V? {
        checkNotNull(view) { "Please setup view" }
        return view?.findViewById<View>(id) as? V
    }

    /**
     * 设置可见状态
     */
    fun setVisibility(id: Int, visibility: Int): XToast {
        findViewById<View>(id)?.visibility = visibility
        return this
    }

    /**
     * 设置文本
     */
    fun setText(text: CharSequence?): XToast {
        return setText(android.R.id.message, text)
    }

    fun setText(id: Int, text: CharSequence?): XToast {
        (findViewById<View>(id) as TextView).text = text
        return this
    }

    /**
     * 设置背景
     */
    fun setBackground(resId: Int): XToast {
        return setBackground(android.R.id.message, resId)
    }

    fun setBackground(id: Int, resId: Int): XToast {
        return setBackground(id, context.resources.getDrawable(resId))
    }

    fun setBackground(drawable: Drawable?): XToast {
        return setBackground(android.R.id.message, drawable)
    }

    fun setBackground(id: Int, drawable: Drawable?): XToast {
        findViewById<View>(id)?.background = drawable
        return this
    }

    /**
     * 设置图片
     */
    fun setImageDrawable(resId: Int): XToast {
        return setImageDrawable(android.R.id.icon, resId)
    }

    fun setImageDrawable(id: Int, resId: Int): XToast {
        return setBackground(id, context.resources.getDrawable(resId))
    }

    fun setImageDrawable(drawable: Drawable?): XToast {
        return setImageDrawable(android.R.id.icon, drawable)
    }

    fun setImageDrawable(id: Int, drawable: Drawable?): XToast {
        (findViewById<View>(id) as ImageView).setImageDrawable(drawable)
        return this
    }

}
