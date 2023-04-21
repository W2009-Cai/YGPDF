package com.yg.pdf.bean

import android.graphics.Bitmap
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

data class FilterStatusBean(
    var filterPosition: Int = 0,
    var filter: GPUImageFilter,
    var path: String,
    var bitmap: Bitmap? = null,
    var rotation: Int = 90

)