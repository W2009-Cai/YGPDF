package com.yg.ad.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.yg.ad.PDFAdLoadSDK;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


/**
 * 插屏广告
 */
public class PDFInterstitialAd {

    private static PDFInterstitialAd instance = new PDFInterstitialAd();
    private String TAG = "TRADAD InterstitialAd";
    private double ecpm = 0.00;
    private boolean isAdmob = false;
    private boolean isLoadingAd = false;

    public static PDFInterstitialAd getInstance() {

        if (instance == null) {
            instance = new PDFInterstitialAd();
        }
        return instance;

    }

    private PDFInterstitialAd() {

    }

    private PDFAdListener PDFAdListener;
    private com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd;

    public void loadAd(Context context, String adUnitId) {
        if (isLoadingAd || isReady()) return;
        isLoadingAd = true;
        Log.i(TAG, "loadAd-加载");

        AdRequest adRequest = new AdRequest.Builder().build();
        com.google.android.gms.ads.interstitial.InterstitialAd.load(context, adUnitId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                        failCount = 0;
                        isLoadingAd = false;
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        if (PDFAdListener != null)
                            PDFAdListener.adLoaded(interstitialAd.getResponseInfo() == null ? "" : interstitialAd.getResponseInfo().getMediationAdapterClassName());
                        PDFInterstitialAd.this.interstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        PDFInterstitialAd.this.interstitialAd = null;
                                        Log.d(TAG, "The ad was dismissed.");

                                        PDFAdLoadSDK.isAdShow = false;
                                        if (PDFAdListener != null) PDFAdListener.adClose();
                                        loadAd(context, adUnitId);
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        PDFInterstitialAd.this.interstitialAd = null;
                                        Log.d(TAG, "The ad failed to show.");

                                        PDFAdLoadSDK.isAdShow = false;
                                        if (PDFAdListener != null)
                                            PDFAdListener.adError(new PDFAdError(adError.getCode(), adError.getMessage()), false);
                                        loadAd(context, adUnitId);
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d(TAG, "The ad was shown.");

                                        PDFAdLoadSDK.isAdShow = true;
                                        if (PDFAdListener != null)
                                            PDFAdListener.adShow(getEcpm(), interstitialAd.getResponseInfo() == null ? "" : interstitialAd.getResponseInfo().getMediationAdapterClassName());
                                    }

                                    @Override
                                    public void onAdClicked() {
                                        super.onAdClicked();
                                        Log.d(TAG, "The ad was clicked.");
                                        if (PDFAdListener != null) PDFAdListener.adClick();
                                    }
                                });
                        interstitialAd.setOnPaidEventListener(adValue -> ecpm = adValue.getValueMicros() * 1.0 / 1000);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        isLoadingAd = false;
                        Log.i(TAG, loadAdError.getMessage());
                        interstitialAd = null;
                        Log.d(TAG, String.format(
                                "domain: %s, code: %d, message: %s",
                                loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage()));
                        if (PDFAdListener != null)
                            PDFAdListener.adError(new PDFAdError(loadAdError.getCode(), loadAdError.getMessage()), true);
                        postLoad(context, adUnitId);
                    }
                });

    }

    public void showAd(Activity activity) {
        if (interstitialAd != null) {
            interstitialAd.show(activity);
        }
    }

    public void entryAdScenario(String sceneId) {
//        if (interstitialAd != null && sceneId != null)
//            interstitialAd.entryAdScenario(sceneId);
    }


    public boolean isReady() {
        if (interstitialAd != null) return true;
        return false;
    }

    public void setPDFAdListener(PDFAdListener PDFAdListener) {
        this.PDFAdListener = PDFAdListener;
    }

    public double getEcpm() {
        try {
            if (isReady()) {
                return ecpm;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void reloadAd(Context context, String adUnitId) {
        if (interstitialAd == null) loadAd(context, adUnitId);
    }

    private int time = 3 * 1000;
    private int failCount = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean runnable = false;

    /**
     * 延迟load
     */
    private void postLoad(Context context, String adUnitId) {
        if (failCount < 2 && interstitialAd == null && !isLoadingAd && !runnable) {
            runnable = true;
            handler.postDelayed(() -> {
                failCount++;
                runnable = false;
                if (!isReady() && !isLoadingAd) {
                    loadAd(context, adUnitId);
                }
            }, time);
        }
    }

}
