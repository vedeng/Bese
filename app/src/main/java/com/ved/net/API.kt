package com.ved.net

import com.ved.net.response.AppBaseUrlResponse
import com.ved.net.response.CheckUpdateResponse
import com.ved.net.response.RegionListResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit调用的API
 */
interface API {

    @POST("app/appUrl")
    fun requestAppBaseUrl(@Body body: RequestBody): Call<AppBaseUrlResponse>

    @POST("app/update")
    fun requestCheckUpdate(@Body body: RequestBody): Call<CheckUpdateResponse>

    /**
     * 地址省市区数据
     */
    @POST("http://mjx.vedeng.com/app/address/regions")
    fun requestAddressData(@Body body: RequestBody): Call<RegionListResponse>

}
