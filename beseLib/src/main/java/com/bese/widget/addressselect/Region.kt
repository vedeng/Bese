package com.bese.widget.addressselect

/**
 * 省市区实体类
 */
data class Region(
        var parentId: Int?,                   // 归属上级ID
        var regionId: Int?,                   // 本级ID
        var regionName: String?     // 本级称号
) {
    constructor() : this(null, null, null)
}