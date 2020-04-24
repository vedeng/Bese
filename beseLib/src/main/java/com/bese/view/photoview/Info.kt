package com.bese.view.photoview

import android.graphics.PointF
import android.graphics.RectF
import android.widget.ImageView.ScaleType

/**
 * 图片信息实体类
 */
data class Info(
    var rect: RectF,
    var imgRect: RectF,
    var widgetRect: RectF,
    var baseRect: RectF,
    var screenCenterPoint: PointF,
    var scale: Float,
    var degrees: Float,
    var scaleType: ScaleType?
)
