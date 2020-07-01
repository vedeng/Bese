package com.ved.net.request

import com.netlib.BaseRequest
import com.ved.net.NetTool
import com.ved.net.response.RegionListResponse
import retrofit2.Call

/**
 * 地址数据请求
 */
class RegionRequest : BaseRequest<RegionRequest.Param, RegionListResponse>() {
    override fun getCall(): Call<RegionListResponse> {
        return NetTool.getApi().requestAddressData(getRequestBody())
    }

    data class Param(var regionId: String?)   // 1
}
