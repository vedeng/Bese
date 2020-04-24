package com.ved.net.request

import com.netlib.BaseRequest
import com.ved.net.NetTool
import com.ved.net.response.AppBaseUrlResponse
import com.ved.net.response.CheckUpdateResponse
import retrofit2.Call

/**
 * 检查更新接口
 */
class CheckUpdateRequest : BaseRequest<Any, CheckUpdateResponse>() {
    override fun getCall(): Call<CheckUpdateResponse> {
        return NetTool.getApi().requestCheckUpdate(getRequestBody())
    }
    data class Param(var version: String?)
}
