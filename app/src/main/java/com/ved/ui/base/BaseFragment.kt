package com.ved.ui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bese.util.ClickUtil
import com.ved.R
import com.ved.entity.event.BackEvent
import com.ved.view.LoadingDialog
import kotlinx.android.synthetic.main.base_content.*
import kotlinx.android.synthetic.main.base_title_bar.*
import org.greenrobot.eventbus.EventBus

/**
 * <Fragment基类>
 */
abstract class BaseFragment : Fragment(), View.OnClickListener {

    private var rootView: View? = null
    protected var baseContext: Context? = null

    private var loadingDialog: LoadingDialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        if (rootView == null) {
            rootView = inflater.inflate(loadView(), container, false)
        } else {
            Log.e("BaseFragment====", "-----rootView缓存了 ------  ${javaClass.canonicalName}")
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        doExecute()
    }

    /**
     * 获取布局
     */
    protected abstract fun loadView(): Int

    /**
     * 控件Init
     */
    protected abstract fun init()

    /**
     * 逻辑处理
     */
    protected abstract fun doExecute()

    override fun onClick(v: View) {
        when (v.id) {
            base_back?.id -> clickLeft()
            base_right?.id -> clickRight()
            else -> {
                clickEvent(v)
            }
        }
    }

    /** 自定义点击事件 */
    open fun clickEvent(v: View?) {
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
    /** Base页面Title左侧按钮点击事件调用，如需要添加点击事件，复写此方法 */
    open fun clickLeft() {
        EventBus.getDefault().post(BackEvent())
    }

    /** Base页面Title右侧按钮点击事件调用，如需要添加点击事件，复写此方法 */
    open fun clickRight() {}

    open fun showLoading() {
        if (isAdded && !isDetached) {
            if (loadingDialog == null) {
                loadingDialog = LoadingDialog(baseContext)
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

    companion object {
        /** Navigation参数配置类 */
        fun getNavOption(enableAnim: Boolean = true, popAim: Int = 0, pushStack: Boolean = true): NavOptions {
            val builder = NavOptions.Builder()
                .setLaunchSingleTop(true)
            if (enableAnim) {
                builder.setEnterAnim(R.anim.anim_new_in)
                builder.setExitAnim(R.anim.anim_old_out)
                builder.setPopEnterAnim(R.anim.anim_old_in)
                builder.setPopExitAnim(R.anim.anim_new_out)
            }
            if (popAim != 0) {
                builder.setPopUpTo(popAim, pushStack)
            }
            return builder.build()
        }
    }

    /**
     * Fragment跳转全局配置
     */
    fun navigate(action: Int, bundle: Bundle? = null, option: NavOptions = getNavOption()) {
        if (!ClickUtil.isFastDoubleClick()) findNavController().navigate(action, bundle, option)
    }

    /**
     * Fragment跳转全局配置
     */
    fun navigateTo(aimId: Int, pushStack: Boolean = true) {
        if (!ClickUtil.isFastDoubleClick()) findNavController().popBackStack(aimId, pushStack)
    }
}