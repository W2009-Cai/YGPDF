package com.yg.ad.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.ads.mediation.facebook.FacebookMediationAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.yg.ad.R

/**
 * 信息流广告
 */
class PDFNativeAdKT {
    private var ecpm = 0.00

    // 获取TP缓存缓存对象
    private var nativeAd: NativeAd? = null
    private var adListener: PDFAdListener? = null
    private var isLoadingAd = false
    fun showAd(adContainer: ViewGroup?, layoutId: Int, adUnitId: String) {
        try {
            if (adContainer == null) return
            val adView =
                LayoutInflater.from(adContainer.context).inflate(layoutId, null) as NativeAdView
            populateNativeAdView(nativeAd!!, adView)
            adContainer.removeAllViews()
            adContainer.addView(adView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setAdListener(PDFAdListener: PDFAdListener?) {
        this.adListener = PDFAdListener
    }

    //加载信息流
    fun loadAd(context: Context?, adUnitId: String?) {
        if (nativeAd != null || isLoadingAd) return
        isLoadingAd = true
        val builder = AdLoader.Builder(context!!, adUnitId!!)
        // OnLoadedListener implementation.
        builder.forNativeAd { nativeAd: NativeAd ->
            isLoadingAd = false
            nativeAd.setOnPaidEventListener { adValue: AdValue ->
                ecpm = adValue.valueMicros * 1.0 / 1000
            }
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            this.nativeAd?.destroy()
            this.nativeAd = nativeAd
            adListener?.adLoaded(
                if (nativeAd.responseInfo == null) "" else nativeAd.responseInfo!!
                    .mediationAdapterClassName
            )
        }
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        builder.withNativeAdOptions(adOptions)
        val adLoader = builder.withAdListener(
            object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    nativeAd = null
                    val error = String.format(
                        "domain: %s, code: %d, message: %s",
                        loadAdError.domain,
                        loadAdError.code,
                        loadAdError.message
                    )
                    Log.d(LOG_TAG, error)
                    adListener?.adError(
                        PDFAdError(
                            loadAdError.code,
                            loadAdError.message
                        )
                    )
                }

                override fun onAdLoaded() {
                    isLoadingAd = false
                    Log.d(LOG_TAG, "onAdLoaded.")
                    super.onAdLoaded()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    Log.d(LOG_TAG, "onAdClosed.")
                    adListener?.adClose()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.d(LOG_TAG, "onAdClicked.")
                    adListener?.adClick()
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    Log.d(LOG_TAG, "onAdImpression.")
                    adListener?.adShow(
                        getEcpm(),
                        if (nativeAd!!.responseInfo == null) "" else nativeAd!!.responseInfo!!
                            .mediationAdapterClassName
                    )
                    nativeAd = null
                    loadAd(context, adUnitId)
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun getEcpm(): Double {
        try {
            return ecpm
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0.0
    }

    fun entryAdScenario(sceneId: String?) {
//        if (tpNative != null && !TextUtils.isEmpty(sceneId)) {
//            tpNative.entryAdScenario(sceneId);
//        }
    }

    val isReady: Boolean
        get() = nativeAd != null
    val isFb: Boolean
        get() {
            if (nativeAd != null) {
                if (FacebookMediationAdapter::class.java.name == nativeAd!!.responseInfo!!.mediationAdapterClassName) {
                    return true
                }
            }
            return false
        }

    /**
     * Populates a [NativeAdView] object with data from a given [NativeAd].
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView   the view to be populated
     */
    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById<View>(R.id.ad_media) as MediaView

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        (adView.headlineView as TextView?)!!.text = nativeAd.headline
        adView.mediaView!!.mediaContent = nativeAd.mediaContent

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (adView.findViewById<View>(R.id.ad_body).visibility == View.VISIBLE) {
            adView.bodyView = adView.findViewById(R.id.ad_body)
            if (nativeAd.body == null) {
                adView.bodyView!!.visibility = View.INVISIBLE
            } else {
                adView.bodyView!!.visibility = View.VISIBLE
                (adView.bodyView as TextView?)!!.text = nativeAd.body
            }
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView!!.visibility = View.INVISIBLE
        } else {
            adView.callToActionView!!.visibility = View.VISIBLE
            (adView.callToActionView as Button?)!!.text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView!!.visibility = View.GONE
        } else {
            (adView.iconView as ImageView?)!!.setImageDrawable(
                nativeAd.icon!!.drawable
            )
            adView.iconView!!.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView.priceView!!.visibility = View.INVISIBLE
        } else {
            adView.priceView!!.visibility = View.VISIBLE
            (adView.priceView as TextView?)!!.text = nativeAd.price
        }
        if (nativeAd.store == null) {
            adView.storeView!!.visibility = View.INVISIBLE
        } else {
            adView.storeView!!.visibility = View.VISIBLE
            (adView.storeView as TextView?)!!.text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            adView.starRatingView!!.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar?)?.rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView!!.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView.advertiserView!!.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView?)!!.text = nativeAd.advertiser
            adView.advertiserView!!.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeAd.mediaContent!!.videoController
        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    super.onVideoEnd()
                }
            }
        }
    }

    fun reloadAd(activity: Activity, adUnitId: String) {
        if (nativeAd == null) loadAd(activity, adUnitId)
    } //    public void onDestroy() {

    //        if (nativeAd != null) {
    ////            nativeAd.destroy();
    //        }
    //    }
    companion object {
        private const val LOG_TAG = "NativeSelfAd"
        val instance = PDFNativeAdKT()
    }
}