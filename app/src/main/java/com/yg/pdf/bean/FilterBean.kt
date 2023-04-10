package com.yg.pdf.bean

import androidx.databinding.BaseObservable
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

//Original
//Gray GPUImageGrayscaleFilter
//Super GPUImageBrightnessFilter(0.0f)
//Enhance GPUImageRGBDilationFilter
//B&W   GPUImageSaturationFilter(0.0f)
//Invert   GPUImageColorInvertFilter

//Adjust
// Contrast ratio   对比度 GPUImageSharpenFilter
// Brightness    亮度  GPUImageBrightnessFilter
// Details     细节  饱和度 GPUImageSaturationFilter
data class FilterBean(
    var gpuImageFilter: GPUImageFilter? = null,
    var isSelect: Boolean = false,
    var title: Int
):BaseObservable()

