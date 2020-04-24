package com.ved.net

import com.netlib.APICreator
import com.netlib.config.NetConfig

object NetTool {

    /**
     * 网络配置定义操作
     */
    private const val BASE_URL = "https://mock.yonyoucloud.com/mock/3936/"
    private const val CONNECT_TIMEOUT = 10
    private const val REQUEST_TIMEOUT = 10
    private const val RESPONSE_TIMEOUT = 10

    /**
     * 网络初始化，定义基域等
     */
    fun initNetConfig() {
        NetConfig.BASE_URL = BASE_URL
        NetConfig.CONNECT_TIMEOUT = CONNECT_TIMEOUT
        NetConfig.REQUEST_TIMEOUT = REQUEST_TIMEOUT
        NetConfig.RESPONSE_TIMEOUT = RESPONSE_TIMEOUT
    }
    private var initFlag = false

    fun getApi() : API {
        if (!initFlag) {
            initNetConfig()
            initFlag = true
        }
        return APICreator.getRetrofit().create(API::class.java)
    }

}