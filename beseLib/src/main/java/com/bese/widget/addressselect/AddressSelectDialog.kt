package com.bese.widget.addressselect

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bese.R

/**
 * < 地址选择弹窗 >
 *     传入需要回显的地址数据时，省市区按顺序不能为空
 */
class AddressSelectDialog(private val mCtx: Context) : DialogFragment() {

    private var selector: AddressSelector? = null
    private var selectListener: OnAddressSelectedListener? = null
    private var loadPresenter: DataLoadPresenter? = null
    private var province: Region? = null
    private var city: Region? = null
    private var district: Region? = null
    private var street: Region? = null

    fun setDefaultRegion(province: Region?, city: Region?, district: Region?, street: Region? = null) : AddressSelectDialog {
        this.province = province
        this.city = city
        this.district = district
        this.street = street
        return this
    }

    fun setAddressSelectListener(listener: OnAddressSelectedListener?) : AddressSelectDialog {
        selectListener = listener
        return this
    }

    fun setDataLoadPresenter(presenter: DataLoadPresenter?) : AddressSelectDialog {
        loadPresenter = presenter
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        selector = AddressSelector(mCtx, loadPresenter, province, city, district, street).apply {
            setOnDialogCloseListener(object : AddressSelector.OnDialogCloseListener {
                override fun dialogClose() { dismiss() }
            })
            setOnAddressSelectedListener(selectListener)
            setAddressDeep(AddressSelector.DEEP_TWO)
        }

        return Dialog(mCtx, R.style.Dialog_Fullscreen_Bottom).apply {
            selector?.view?.run {
                setContentView(this)
                window?.run {
                    setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    setDimAmount(0.6f)
                    setGravity(Gravity.BOTTOM)
                    setCanceledOnTouchOutside(true)
                }
            }
        }
    }

}