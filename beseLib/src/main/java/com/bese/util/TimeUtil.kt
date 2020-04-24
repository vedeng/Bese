package com.bese.util

object TimeUtil {

    /**
     * 获取剩余时间
     *      内在规则：
     *      精确到H，最高不能有99小时，否则不返回【可修改规则】
     *      精确到D，展示的是 00:00:00:00 原则上不推荐使用这种场景，难以区分天数和时数
     */
    fun getTimeRest(ms: Long?, withDay: Boolean = false) : String? {
        ms?.run {
            if (ms <= 0) return ""
            val sec = ms / 1000              // 秒数
            val s = sec % 60                     // 秒位
            val m = sec / 60 % 60          // 分位
            val h = sec / 3600                 // 时位
            val show = getTimeCover(h).plus(":").plus(getTimeCover(m)).plus(":").plus(getTimeCover(s))
            if (withDay) {
                val d = sec / 3600 / 24       // 天位，如果需要
                return if (d > 99) { "" } else { getTimeCover(h).plus(show) }
            } else {
                return if (h > 99) { "" } else { show }
            }
        }
        return ""
    }

    /**
     * 获取格式化的计时器字符串（不足两位补0）
     */
    fun getTimeCover(number: Long): String {
        val str = number.toString()
        return if (str.length < 2) "0$str" else str
    }

}