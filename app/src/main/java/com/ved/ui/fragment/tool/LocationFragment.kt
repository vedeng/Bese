package com.ved.ui.fragment.tool

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import com.ved.R
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_location.*
import java.util.*

class LocationFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_location
    }

    override fun init() {
        ip_loc?.setOnClickListener {
            val mask = NetworkUtils.getIpAddressByWifi()
            Log.e("macMoask====", mask)
            val ip = DeviceUtils.getMacAddress()
            ip_name?.text = "当前IP：$ip"
        }

        loc?.setOnClickListener {
            XXPermissions.with(requireActivity()).permission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .request(object : OnPermission {
                    override fun noPermission(denied: MutableList<String>?, quick: Boolean) {
                        ToastUtils.showShort("拒绝了权限~")
                        Log.e("拒绝了啥====", denied?.get(0) ?: "-")
                    }

                    override fun hasPermission(granted: MutableList<String>?, isAll: Boolean) {
                        getLocation()
                        Log.e("授权了啥====", granted?.get(0) ?: "-")
                    }
                })
        }
    }

    override fun doExecute() {
    }

    /**
     * 原生定位，会去获取上次GPS的位置。可能不准
     */
    private fun getLocation() {
        val c = Criteria().apply {
            powerRequirement = Criteria.POWER_MEDIUM                   // 低功耗
            accuracy = Criteria.ACCURACY_FINE                         // 定位模式, 原生定位不可使用高精度模式
            bearingAccuracy = Criteria.ACCURACY_FINE          // 定向模式
            isAltitudeRequired = false                          // 不使用海拔模式
            isBearingRequired = false                           // 不使用定向
            isCostAllowed = false                                   // 不花费成本
        }

        val locManager = baseContext?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val bestProvider = locManager?.getBestProvider(c, true)

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ToastUtils.showShort("缺失权限~")
            return
        }
        var location = bestProvider?.run { locManager.getLastKnownLocation(this) }
        if (location == null) {
            locManager?.run {
                when {
                    isProviderEnabled(LocationManager.GPS_PROVIDER) -> location = getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> location = getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    isProviderEnabled(LocationManager.PASSIVE_PROVIDER) -> location = getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                }
            }
        }
        if (location == null) {
            ToastUtils.showShort("定位失败了~")
            /*
            *原生定位功能依靠设备的GPS服务或者网关
            */
            return
        }
        location?.run {
            val geoCoder = Geocoder(baseContext, Locale.CHINESE)
            val addressList = geoCoder.getFromLocation(latitude, longitude, 1)
            if (addressList.isNotEmpty()) {
                val address = addressList[0]
                Log.e("地址信息====", "${address.countryName}  -- ${address.locality}  - ${address.getAddressLine(0)}")
                Log.e("经纬度====", "${address.latitude}  -- ${address.longitude}  - ${address.adminArea} -- ${address.toString()}")
                ToastUtils.showShort("${address.countryName}  -- ${address.locality}")
                setLoc("${address.countryName}  -- ${address.locality}")
            }
        }
    }

    private fun setLoc(m: String?) {
        loc_name?.text = "当前位置：$m"
    }

}