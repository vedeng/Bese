package com.ved.net.response

import com.bese.widget.addressselect.Region
import com.netlib.BaseResponse

data class AppBaseUrlResponse(var data: AppBaseUrlData) : BaseResponse()
data class CheckUpdateResponse(var data: CheckUpdateData) : BaseResponse()
data class RegionListResponse(var data: ArrayList<Region?>?) : BaseResponse()
