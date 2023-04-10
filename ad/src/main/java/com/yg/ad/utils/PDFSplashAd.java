package com.yg.ad.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.yg.ad.PDFAdLoadSDK;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

public class PDFSplashAd {
    private static final PDFSplashAd instance = new PDFSplashAd();
    private final String TAG = "SplashAd";
    private PDFAdListener PDFAdListener;
    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    private boolean isShowingAd = false;
    private double currentEcpm = 0.00;

    /**
     * Keep track of the time an app open ad is loaded to ensure you don't show an expired ad.
     */
    private long loadTime = 0;

    /**
     * Constructor.
     */
    public PDFSplashAd() {

    }

    public static PDFSplashAd getInstance() {
        return instance;

    }

    public void setAdListener(PDFAdListener PDFAdListener) {
        this.PDFAdListener = PDFAdListener;
    }

    /**
     * Load an ad.
     *
     * @param context  the context of the activity that loads the ad
     * @param adUnitId 广告位id
     */
    public void loadAd(Context context, String adUnitId) {
        // Do not load ad if there is an unused ad or one is already loading.
        if (isLoadingAd || isReady()) {
            return;
        }

        isLoadingAd = true;
        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(
                context,
                adUnitId,
                request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    /**
                     * Called when an app open ad has loaded.
                     *
                     * @param ad the loaded app open ad.
                     */
                    @Override
                    public void onAdLoaded(AppOpenAd ad) {
                        appOpenAd = ad;
                        isLoadingAd = false;
                        loadTime = (new Date()).getTime();
                        Log.d(TAG, "onAdLoaded.");
                        if (PDFAdListener != null) {
                            appOpenAd.getResponseInfo();
                            PDFAdListener.adLoaded(appOpenAd.getResponseInfo().getMediationAdapterClassName());
                        }
                    }

                    /**
                     * Called when an app open ad has failed to load.
                     *
                     * @param loadAdError the error.
                     */
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        isLoadingAd = false;
                        Log.d(TAG, "onAdFailedToLoad: " + loadAdError.getCode() + loadAdError.getMessage());
                        if (PDFAdListener != null)
                            PDFAdListener.adError(new PDFAdError(loadAdError.getCode(), loadAdError.getMessage()));
                    }
                });
    }

    /**
     * Check if ad was loaded more than n hours ago.
     */
    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    /**
     * Check if ad exists and can be shown.
     */
    public boolean isReady() {
        // Ad references in the app open beta will time out after four hours, but this time limit
        // may change in future beta versions. For details, see:
        // https://support.google.com/admob/answer/9341964?hl=en
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }


    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity the activity that shows the app open ad
     * @param adUnitId 广告位id
     */
    public void showAdIfAvailable(final Activity activity, String adUnitId) {
        // If the app open ad is already showing, do not show the ad again.
        if (activity == null) {
            return;
        }

        if (isShowingAd) {
            Log.d(TAG, "The app open ad is already showing.");
            return;
        }

        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (!isReady()) {
            Log.d(TAG, "The app open ad is not ready yet.");
            loadAd(activity, adUnitId);
            return;
        }

        Log.d(TAG, "Will show ad.");
        appOpenAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    /** Called when full screen content is dismissed. */
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null;
                        isShowingAd = false;
                        PDFAdLoadSDK.isAdShow = false;
                        Log.d(TAG, "onAdDismissedFullScreenContent.");
                        if (PDFAdListener != null) PDFAdListener.adClose();
                        loadAd(activity, adUnitId);
                    }

                    /** Called when fullscreen content failed to show. */
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        appOpenAd = null;
                        isShowingAd = false;
                        PDFAdLoadSDK.isAdShow = false;
                        Log.d(TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                        if (PDFAdListener != null)
                            PDFAdListener.adError(new PDFAdError(adError.getCode(), adError.getMessage()), false);
                        loadAd(activity, adUnitId);
                    }

                    /** Called when fullscreen content is shown. */
                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.d(TAG, "onAdShowedFullScreenContent.");

                        PDFAdLoadSDK.isAdShow = true;
                        if (PDFAdListener != null)
                            PDFAdListener.adShow(getEcpm(), appOpenAd.getResponseInfo().getMediationAdapterClassName());
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        Log.d(TAG, "onAdClicked.");
                        if (PDFAdListener != null) PDFAdListener.adClick();
                    }
                });
        appOpenAd.setOnPaidEventListener(adValue -> currentEcpm = adValue.getValueMicros() * 1.0 / 1000);
        isShowingAd = true;
        appOpenAd.show(activity);
    }


    public double getEcpm() {
        return currentEcpm;
    }
}
