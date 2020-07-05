package com.bese.util

/**
 * 控件点击控制工具
 */
object ClickUtil {

    private const val DIFF: Long = 500
    private var lastClickTime: Long = 0
    private var lastButtonId = -1

    /**
     * 判断两次点击的间隔，如果小于DIFF，则认为是多次无效点击
     * @return 是否双击
     */
    fun filterGlobal(diff: Long = DIFF): Boolean {
        return filter(-1, diff)
    }

    fun filter(buttonId: Int?, diff: Long = DIFF) : Boolean {
        return isFastDoubleClick(buttonId, diff)
    }

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     */
    fun isFastDoubleClick(buttonId: Int?, diff: Long = DIFF): Boolean {
        val time = System.currentTimeMillis()
        val timeD = time - lastClickTime
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
            return true
        }
        lastClickTime = time
        lastButtonId = buttonId ?: -1
        return false
    }


}