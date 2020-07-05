package com.bese.util

/**
 * 控件点击控制工具
 */
object ClickUtil {

    private const val SLOW_DIFF: Long = 1600
    private const val DIFF: Long = 500
    private var lastClickTime: Long = 0
    private var lastButtonId = -1
    private var lastClickTimeToQuitApp: Long = 0

    /**
     * 判断两次点击的间隔，如果小于DIFF，则认为是多次无效点击
     * @return 是否双击
     */
    fun isFastDoubleClick(): Boolean {
        return isFastDoubleClick(-1, DIFF)
    }

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     *
     * @param buttonId 控件
     * @param diff 双击判定间隔
     * @return
     */
    private fun isFastDoubleClick(buttonId: Int?, diff: Long = DIFF): Boolean {
        val time = System.currentTimeMillis()
        val timeD = time - lastClickTime
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
            return true
        }
        lastClickTime = time
        lastButtonId = buttonId ?: -1
        return false
    }

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     *
     * @param buttonId 控件
     * @param diff 双击判定间隔
     * @return
     */
    fun isSlowDoubleClick(buttonId: Int?): Boolean {
        return isFastDoubleClick(buttonId, SLOW_DIFF)
    }

    /**
     * 双击退出APP操作
     */
    fun clickToQuitApp(): Boolean {
        val time = System.currentTimeMillis()
        val oldTime = lastClickTimeToQuitApp
        lastClickTimeToQuitApp = time
        return time - oldTime <= 2000
    }
}