package com.yg.pdf

import ando.file.core.FileOperator
import android.app.Application
import com.drake.brv.utils.BRV
import com.yg.ad.PDFAdLoadSDK
import com.yg.pdf.weight.NotificationUtils

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FileOperator.init(this, BuildConfig.DEBUG)
        BRV.modelId = BR.m
//        PDFAdLoadSDK.getInstance().setDebug(BuildConfig.DEBUG)
        PDFAdLoadSDK.getInstance().initAdSDK(this)
        AppOpenManager(this)
        NotificationUtils.initNotificationCompat(this)
    }
}