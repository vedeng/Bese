package com.ved.entity.adapter

import com.chad.library.adapter.base.entity.SectionEntity

data class StickyItemEntity(
    var name: String? = null,
    var url: String? = null,
    var price: String? = null,
    var entityType: Int = 0,
    var headFlag: Boolean = false
) : SectionEntity {
    companion object {
        const val ITEM_TYPE_SPAN = 1
    }

    override val isHeader: Boolean
        get() = headFlag

    override val itemType: Int
        get() = when (entityType) {
            1 -> ITEM_TYPE_SPAN
            else -> 0
        }
}
