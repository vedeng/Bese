package com.bese.widget.addressselect

/**
 * 省市区实体类
 */
data class Region(
        var parentId: Int?, // 1
        var regionId: Int?, // 2
        var regionName: String? // 北京
) {
    constructor() : this(null, null, null)
}