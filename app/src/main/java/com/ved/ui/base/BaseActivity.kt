package com.ved.ui.base

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.BarUtils
import com.ved.R
import com.ved.util.TouchTracker
import com.ved.view.LoadingDialog
import kotlinx.android.synthetic.main.base_content.*
import kotlinx.android.synthetic.main.base_title_bar.*

/**
 * 基类Activity 处理公共布局
 */
abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {

    private var loadingDialog: LoadingDialog? = null

    private var motionTracker: TouchTracker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 用户全Activity做手势捕捉，可穿透用户触摸具体的View对象
        motionTracker = TouchTracker(window?.decorView as? ViewGroup)

        // 对通知栏做样式改动
        setStatusBarColor(Color.WHITE)

        // 针对部分页面不初始化标题栏，返回按钮就无点击事件，所以返回按钮预先设定点击事件
        base_back?.setOnClickListener(this)

        setContentView(loadView())
        init()
        doExecute()
    }

    /**
     * 改变状态栏样式：
     *      默认使用白底黑字。
     *      如果需要占用，在布局中去除fitsSystemWindows属性。
     *
     * @param statusBarColor 状态栏背景颜色
     * @param fakeStatusBar 有的页面根Layout不是白色，会导致StatusBar跟随根Layout的底色。此属性可生成一个假背景，颜色跟随statusBarColor
     */
    protected fun setStatusBarColor(@ColorInt statusBarColor: Int, fakeStatusBar: Boolean = true, isLightMode: Boolean = true) {
        BarUtils.setStatusBarLightMode(this, isLightMode)
        BarUtils.setStatusBarColor(this, statusBarColor, fakeStatusBar)
    }

    /**
     * 初始化标题：
     *      部分页面可能不引入标题栏Layout。设置就不生效。
     */
    protected fun initTitle(title: String, showRight: Boolean = true, resId: Int = R.string.icon_app_more) {
        base_title?.text = "$title"
        base_back?.setOnClickListener(this)
        base_right?.setOnClickListener(this)
        if (showRight) {
            base_right?.visibility = View.VISIBLE
            base_right?.setText(resId)
        } else {
            base_right?.visibility = View.GONE
        }
    }

    fun initContent(tvArea: String, tvRole: String, tvInteract: String, tvStyle: String) {
        tv_use_area?.text = tvArea
        tv_use_role?.text = tvRole
        tv_interact?.text = tvInteract
        tv_style?.text = tvStyle
    }

    /** 加载页面主布局-layout */
    protected abstract fun loadView() : Int
    /** 做一些初始化，如监听，和需要初始设置控件value的操作 */
    protected abstract fun init()
    /** 主逻辑代码块，初始化之后的逻辑操作和调用在此执行 */
    protected abstract fun doExecute()

    override fun onClick(v: View) {
        Log.e("Clickable ID === ", "${v.id}")
        when(v.id) {
            base_back?.id -> clickLeft()
            base_right?.id -> clickRight()
            else -> { clickEvent(v) }
        }
    }

    /** 自定义点击事件 */
    open fun clickEvent(v: View?) {
    }

    /** Base页面Title左侧按钮点击事件调用，如需要添加点击事件，复写此方法 */
    open fun clickLeft() { finish() }

    /** Base页面Title右侧按钮点击事件调用，如需要添加点击事件，复写此方法 */
    open fun clickRight() {}

    /**
     * 此处捕获用户手势事件，不论是否可点击，都可以先从这里走。
     * 但范围仅限所有的Activity，无法对Dialog，DialogFragment做捕获。
     */
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        // 可获取点击位置，屏幕XY值，左上角屏幕顶点(0,0)，右下角(width, height)
//        Log.e("分发Location===", "X: ${event?.rawX}  Y:${event?.rawY}")
        when (event?.action) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                // 多汁触控，不执行
                Log.e("分发===", "手指按下")
            }
            MotionEvent.ACTION_DOWN -> {
                Log.e("分发===", "按下")
            }
            MotionEvent.ACTION_MOVE -> {
                Log.e("分发===", "移动")
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                /*
                * 获取到当前手势抬起时所在的控件View
                *       view?.width      控件宽px
                *       view?.height     控件高px
                *       view?.pivotX  view?.pivotY 控件的中心点坐标。
                *       view?.isClickable 控件是否可点，不可点击不会有点击事件
                */
                val view = motionTracker?.findTargetView()
                Log.e("分发===", "松开 - ${view?.id}     +     ${view?.tag}")
            }
            MotionEvent.ACTION_POINTER_UP -> {
                Log.e("分发===", "手指抬起")
            }
            else -> {
                Log.e("其他触摸action类型分发===", "${event?.action}")
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        res.configuration.fontScale = 1f
        res.updateConfiguration(res.configuration, res.displayMetrics)
        return res
    }

    open fun showLoading() {
        if (!isDestroyed) {
            if (loadingDialog == null) {
                loadingDialog = LoadingDialog(this)
            }
            loadingDialog?.show()
        }
    }

    open fun hideLoading() {
        loadingDialog?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog?.hide()
        loadingDialog = null
    }

}
