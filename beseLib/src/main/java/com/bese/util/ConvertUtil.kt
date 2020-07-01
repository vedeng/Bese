package com.bese.util

import com.blankj.utilcode.util.ScreenUtils

/**
 * 转换工具类
 */
object ConvertUtil {

    /**
     * ScreenMatch自动尺寸转换不适用于使用固定数值的尺寸
     * 提供工具类，适用于动态配置px值。如果需要用dp，用计算的值 ➗ ScreenUtils.getScreenDensity()
     * 以屏幕宽度750份均分为基准。可自定义
     */
    fun getSizedPx(dp: Float, average: Int = 750) : Float {
        return dp * ScreenUtils.getScreenWidth() / average
    }
}