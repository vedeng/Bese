package com.ved.ui.fragment.select

import android.os.Handler
import android.os.Message
import com.bese.widget.addressselect.AddressSelectDialog
import com.bese.widget.addressselect.DataLoadPresenter
import com.bese.widget.addressselect.OnAddressSelectedListener
import com.bese.widget.addressselect.Region
import com.blankj.utilcode.util.ToastUtils
import com.netlib.BaseCallback
import com.ved.R
import com.ved.net.request.RegionRequest
import com.ved.net.response.RegionListResponse
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_select_address.*

class SelectAddressFragment : BaseFragment(), DataLoadPresenter {

    private var mSelectListener: OnAddressSelectedListener? = null

    var selectProvince: Region? = null
    var selectCity: Region? = null
    var selectDistrict: Region? = null
    var selectStreet: Region? = null

    override fun loadView(): Int {
        return R.layout.fragment_select_address
    }

    override fun init() {
        mSelectListener = object : OnAddressSelectedListener {
            override fun onAddressSelected(province: Region?, city: Region?, county: Region?, street: Region?) {
                val address = "${province?.regionName} - ${city?.regionName} - ${county?.regionName} - ${street?.regionName}"
                ToastUtils.showShort(address)
                tv_address?.text = address
                selectProvince = province
                selectCity = city
                selectDistrict = county
                selectStreet = street
            }
        }

        tv_select_address?.setOnClickListener {
            AddressSelectDialog(requireContext())
                .setAddressSelectListener(mSelectListener)
                .setDataLoadPresenter(this)
                .setDefaultRegion(selectProvince, selectCity, selectDistrict, selectStreet)
                .show(requireActivity().supportFragmentManager, "SelectAddress")
        }
    }

    override fun doExecute() {

    }

    override fun onAddressDataLoad(id: String?, type: Int, handler: Handler) {
        RegionRequest().request(RegionRequest.Param(id), object : BaseCallback<RegionListResponse>() {
            override fun onSuccess(response: RegionListResponse?) {
                response?.data?.run {
                    handler.sendMessage(Message.obtain(handler, type, this))
                    return
                }
            }
        })
    }

}