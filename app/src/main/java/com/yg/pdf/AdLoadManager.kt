package com.yg.pdf

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import com.yg.ad.utils.*
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.LogUtils.getConfig
import com.yg.pdf.utils.RemoteConfigManager

/**
 */
object AdLoadManager {
    private const val AD_INTERSTITIAL = "ca-app-pub-2180123548047178/3028680878"
    private const val AD_SPLASH = "ca-app-pub-2180123548047178/8895536480"
    private const val AD_NATIVE = "ca-app-pub-2180123548047178/2112799428"
    var isGp: Boolean = false
    var isSplashShow: Boolean = true

    fun setIsGp(isGp: Boolean) {
        AdLoadManager.isGp = isGp;
    }

    fun setIsSplashShow(isSplashShow: Boolean) {
        AdLoadManager.isSplashShow = isSplashShow;
    }

    /**
     * 加载开屏
     * 非GP用户若不能展示广告，则不加载广告
     * */
    fun loadSplash(context: Context) {
        if (canShowGpAd() && isSplashShow) {
            PDFSplashAd.getInstance().loadAd(context, AD_SPLASH)
        }
    }

    /**
     * 判断开屏是否展示
     * 非GP用户若不能展示广告，则不加载广告
     * */
    fun splashIsReady(): Boolean {
        if (canShowGpAd() && isSplashShow) {
            return PDFSplashAd.getInstance().isReady
        }
        return false
    }

    /**
     * 加载插屏
     * 非GP用户若不能展示广告，则不加载广告
     * */
    fun loadInterstitial(context: Context) {
        if (canShowGpAd()) {
            PDFInterstitialAd.getInstance().loadAd(context, AD_INTERSTITIAL)
        }
    }

    /**
     * 加载信息流
     * 非GP用户若不能展示广告，则不加载广告
     */
    fun loadNative(activity: Activity) {
        if (canShowGpAd()) {
            PDFNativeAdKT.instance.loadAd(activity, AD_NATIVE)
        }
    }

    /*显示开屏广告*/
    fun showSplashAd(context: Activity?, cwAdListener: PDFAdListener?) {
        //可用于全局埋点
        PDFSplashAd.getInstance().setAdListener(object :
            PDFAdListener {

            override fun adLoaded(adNetworkId: String?) {
                log("Splash adLoaded")
                cwAdListener?.adLoaded(adNetworkId)
            }

            override fun adShow(ecpm: Double?, adNetworkId: String?) {
                log("Splash adShow")
//                AnalyticsUtils.logEventAll("splash_ad_succeed")
//                if (CwSPUtils.isTodayTime()) {
//                    AnalyticsUtils.logEvent("new_splash_ad_show")
//                } else {
//                    AnalyticsUtils.logEvent("old_splash_ad_show")
//                }
                AdLoadManagerUtil.getInstance().calculateEcpm(ecpm)
                cwAdListener?.adShow(ecpm, adNetworkId)
            }

            override fun adClick() {
//                AnalyticsUtils.logEventAll("splash_ad_click")
                cwAdListener?.adClick()
            }

            override fun adClose() {
                log("Splash adClose")
                cwAdListener?.adClose()
            }

            override fun adComplete() {
                log("Splash adComplete")
                cwAdListener?.adComplete()
            }

            override fun adError(adError: PDFAdError?, isLoadFailed: Boolean) {
                log("Splash adError " + adError?.errorMsg)
                val bundle = Bundle()
                bundle.putString("splash_ad_error", adError?.errorMsg)
//                AnalyticsUtils.logEvent("splash_error", bundle)
                cwAdListener?.adError(adError, isLoadFailed)
            }

        })

        PDFSplashAd.getInstance().showAdIfAvailable(context, AD_SPLASH);
    }

    /**
     * 信息流广告
     * */
    fun showNative(viewGroup: ViewGroup, adListener: PDFAdListener?) {
        //可用于全局埋点
        PDFNativeAdKT.instance.setAdListener(object :
            PDFAdListener {

            override fun adLoaded(adNetworkId: String?) {
                log("Native adLoaded")
                adListener?.adLoaded(adNetworkId)
            }

            override fun adShow(ecpm: Double?, adNetworkId: String?) {
                log("Native adShow")
//                AnalyticsUtils.logEventAll("native_ad_succeed")
                AdLoadManagerUtil.getInstance().calculateEcpm(ecpm)
                adListener?.adShow(ecpm, adNetworkId)
            }

            override fun adClick() {
                log("Native adClick")
//                AnalyticsUtils.logEventAll("native_ad_click")
                adListener?.adClick()
            }

            override fun adClose() {
                log("Native adClose")
                adListener?.adClose()
            }

            override fun adComplete() {
                log("Native adComplete")
                adListener?.adComplete()
            }

            override fun adError(adError: PDFAdError?, isLoadFailed: Boolean) {
                log("Native adError " + adError?.errorMsg)
                val bundle = Bundle()
                bundle.putString("navive_ad_error", adError?.errorMsg)
//                AnalyticsUtils.logEvent("navive_error", bundle)
                adListener?.adError(adError, isLoadFailed)
            }

        })

        if (PDFNativeAdKT.instance.isFb) {
            PDFNativeAdKT.instance
                .showAd(viewGroup, R.layout.tp_native_ad_fb_small, AD_NATIVE)
        } else {
            PDFNativeAdKT.instance
                .showAd(viewGroup, R.layout.tp_native_ad_nofb_small, AD_NATIVE)
        }
    }

    /*插页式广告*/
    fun showInterstitialAd(activity: Activity, scenarioId: String, adListener: PDFAdListener?) {
        //可用于全局埋点
        PDFInterstitialAd.getInstance().setPDFAdListener(object : PDFAdListener {
            override fun adLoaded(adNetworkId: String?) {
                log("Interstitial adLoaded")
                adListener?.adLoaded(adNetworkId)
            }

            override fun adShow(ecpm: Double?, adNetworkId: String?) {
                log("Interstitial adShow")
//                AnalyticsUtils.logEventAll("interstitial_ad_succeed")
                AdLoadManagerUtil.getInstance().calculateEcpm(ecpm)
                adListener?.adShow(ecpm, adNetworkId)
            }

            override fun adClose() {
                log("Interstitial adClose")
                adListener?.adClose()
            }

            override fun adClick() {
//                AnalyticsUtils.logEventAll("interstitial_ad_click")
                adListener?.adClick()
            }

            override fun adComplete() {
                log("Interstitial adComplete")
                adListener?.adComplete()
            }

            override fun adError(adError: PDFAdError?, isLoadFailed: Boolean) {
                adListener?.adError(adError, isLoadFailed)
                log("Interstitial adError " + adError?.errorMsg)
                log("Native adError " + adError?.errorMsg)
                val bundle = Bundle()
                bundle.putString("interstitial_ad_error", adError?.errorMsg)
//                AnalyticsUtils.logEvent("interstitial_error", bundle)

            }

        })
        PDFInterstitialAd.getInstance().showAd(activity)
        loadInterstitial(activity)
    }

    fun canShowGpAd(): Boolean {
        //debug模式 开启非GP广告
        return if (isGp || BuildConfig.DEBUG) {
            true
        } else {
            RemoteConfigManager.getmRemoteConfig()?.inside?.isGP == 1
        }
    }

    private fun log(str: String) {
        LogUtils.d("AdLoadManager", str)
    }
}