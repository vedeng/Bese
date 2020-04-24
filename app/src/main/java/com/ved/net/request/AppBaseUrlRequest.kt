package com.ved.net.request

import com.netlib.BaseRequest
import com.ved.net.NetTool
import com.ved.net.response.AppBaseUrlResponse
import retrofit2.Call

/**
 * APP域名更换接口
 */
class AppBaseUrlRequest : BaseRequest<Any, AppBaseUrlResponse>() {
    override fun getCall(): Call<AppBaseUrlResponse> {
        return NetTool.getApi().requestAppBaseUrl(getRequestBody())
    }
    data class Param(var type: String?)
}
