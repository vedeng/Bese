package com.bese.widget.addressselect

import android.os.Handler

/**
 * < 省市区街道数据的获取助理 >
 */
class AddressDataHelper internal constructor(private val handler: Handler) {

    private var mLoadPresenter: DataLoadPresenter? = null

    /**
     * 外部设定数据读取方式
     *      可选方式：数据库读取 - 不好更新
     *      网络数据读取 - 读取稍慢
     */
    fun setDataLoad(loadPresenter: DataLoadPresenter?) {
        mLoadPresenter = loadPresenter
    }

    /**
     * 请求省份列表
     */
    fun getProvinceList(id: String?) {
        requestData(id, AddressSelector.WHAT_PROVINCES_PROVIDED)
    }

    /**
     * 请求城市列表
     */
    fun getCityList(provinceId: String?) {
        requestData(provinceId, AddressSelector.WHAT_CITIES_PROVIDED)
    }

    /**
     * 请求区县列表
     */
    fun getDistrictList(cityId: String?) {
        requestData(cityId, AddressSelector.WHAT_COUNTIES_PROVIDED)
    }

    /**
     * 请求区县列表
     */
    fun getStreetList(cityId: String?) {
        requestData(cityId, AddressSelector.WHAT_STREETS_PROVIDED)
    }

    /**
     * 请求 省/市区/街 列表
     */
    fun requestData(id: String?, type: Int) {
        mLoadPresenter?.onAddressDataLoad(id, type, handler)
    }

}