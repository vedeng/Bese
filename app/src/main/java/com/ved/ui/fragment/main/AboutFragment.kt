package com.ved.ui.fragment.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import com.ved.R
import com.ved.ui.base.BaseFragment
import java.lang.Exception

class AboutFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_about
    }

    override fun init() {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doExecute() {
        checkPermission()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkPermission() {
        XXPermissions.with(requireActivity()).permission(Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS)
            .request(object : OnPermission {
                override fun noPermission(denied: MutableList<String>?, quick: Boolean) {
                    ToastUtils.showShort("无权限啊~")
                }

                override fun hasPermission(granted: MutableList<String>?, isAll: Boolean) {
                    getPhone()
                }
            })
    }

    @SuppressLint("MissingPermission")
    private fun getPhone() {

        val startTime = System.currentTimeMillis()
        val m : TelephonyManager = baseContext?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            val phone = m.getLine1Number()
            ToastUtils.showShort("$phone  - ${System.currentTimeMillis() - startTime}")
        } catch (e: Exception) {
            Log.e("PHONE_ERROR: ", "${e.message}")
        }
    }

}