package com.bese.widget.addressselect

import android.os.Handler

/**
 * 地址数据读取助手
 */
interface DataLoadPresenter {

    fun onAddressDataLoad(id: String?, type: Int, handler: Handler)

}