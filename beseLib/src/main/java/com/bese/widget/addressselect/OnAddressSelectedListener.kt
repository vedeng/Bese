package com.bese.widget.addressselect

/**
 * 地址选择监听
 */
public interface OnAddressSelectedListener {

    fun onAddressSelected(
        province: Region?,
        city: Region?,
        county: Region?,
        street: Region?
    )

}