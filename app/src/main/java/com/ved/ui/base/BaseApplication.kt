package com.ved.ui.base

import android.app.Application
import android.graphics.Color
import android.view.Gravity
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.netlib.APICreator

/**
 * <基>
 */
class BaseApplication : Application() {

    companion object {
        var ctx: BaseApplication? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        ctx = this
        // 初始化网络BaseUrl
        APICreator.init("https://mock.yonyoucloud.com/mock/3936/")

        initUtils()
    }

    private fun initUtils() {
        Utils.init(this)
        LogUtils.getConfig().run { isLogSwitch = true; globalTag = "VEUI"; setBorderSwitch(false) }
        ToastUtils.setBgColor(Color.parseColor("#a0000000"))
        ToastUtils.setMsgColor(Color.parseColor("#FFFFFF"))
        ToastUtils.setGravity(Gravity.CENTER, 0, 300)
    }

}
