package com.yg.ad;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.format.DateUtils;
import android.util.Log;

import com.facebook.ads.AdSettings;
import com.yg.ad.utils.PDFSPUtils;
import com.facebook.CustomTabMainActivity;
import com.facebook.FacebookActivity;
import com.facebook.FacebookSdk;
import com.google.android.gms.ads.AdActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.common.api.GoogleApiActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 广告SDK简单调用
 */
public class PDFAdLoadSDK {
    public static boolean isAdShow = false;//是否已经显示广告
    public static final String LOG_TAG = "AdLoadSDK";
    private Context context;

    private static class SingletonHolder {
        private static final PDFAdLoadSDK instance = new PDFAdLoadSDK();
    }

    public static PDFAdLoadSDK getInstance() {
        return SingletonHolder.instance;
    }

    private PDFAdLoadSDK() {
    }

    public Context getContext() {
        return context.getApplicationContext();
    }

    public void setDebug(boolean debug) {
        if (debug) {
            List<String> testDeviceIds = Arrays.asList("48B8F049CAA1E9BD6DD7429316F6F630", "BE113C5AF2BBE68321F229C2363B37BF");
            RequestConfiguration configuration =
                    new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);

            //Google Play Advertising id
            AdSettings.addTestDevice("f424e232-2ce0-46f1-a781-4d592af6b20e");
//            AppsFlyerLib.getInstance().setDebugLog(true);
        }
    }

    public void initAdSDK(Context context) {
        this.context = context;
//        AppLovinPrivacySettings.setHasUserConsent(true, context);

        MobileAds.initialize(context, initializationStatus -> {
        });
//        AppsFlyerLib.getInstance().init(context.getString(R.string.AF_DEV_KEY), conversionListener, context);
//        AppsFlyerLib.getInstance().start(context);
//        new FacebookSdk();
    }

//    private AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
//        @Override
//        public void onConversionDataSuccess(Map<String, Object> conversionDataMap) {
//            for (String attrName : conversionDataMap.keySet())
//                Log.d(LOG_TAG, "Conversion attribute: " + attrName + " = " + conversionDataMap.get(attrName));
//            String status = Objects.requireNonNull(conversionDataMap.get("af_status")).toString();
//            //Organic自然 Non-organic非自然
//            if (status.equals("Non-organic")) {
//                // Business logic for Organic conversion goes here.
//                PDFSPUtils.putBoolean(context, "install_channel", false);
//            } else {
//                // Business logic for Non-organic conversion goes here.
//                PDFSPUtils.putBoolean(context, "install_channel", true);
//            }
//        }
//
//        @Override
//        public void onConversionDataFail(String errorMessage) {
//            Log.d(LOG_TAG, "error getting conversion data: " + errorMessage);
//        }
//
//        @Override
//        public void onAppOpenAttribution(Map<String, String> attributionData) {
//            // Must be overriden to satisfy the AppsFlyerConversionListener interface.
//            // Business logic goes here when UDL is not implemented.
//        }
//
//        @Override
//        public void onAttributionFailure(String errorMessage) {
//            // Must be overriden to satisfy the AppsFlyerConversionListener interface.
//            // Business logic goes here when UDL is not implemented.
//            Log.d(LOG_TAG, "error onAttributionFailure : " + errorMessage);
//        }
//
//    };

    /**
     * 判断是否为自然量用户
     *
     * @return true 自然量 不弹应用外
     */
    public boolean isOrganic() {
        try {
            boolean status = PDFSPUtils.getBoolean(context, "install_channel", true);
            Log.d(LOG_TAG, "isOrganic    " + status);
            return status;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 判断是否新用户
     *
     * @return
     */
    public boolean isNewUser() {
        Long firstIntallTime = PDFSPUtils.getLong(context, "firstIntallTime", 0L);
        if (firstIntallTime > 0L) {
        } else {
            firstIntallTime = getFirstInstallTime();
            PDFSPUtils.putLong(context, "firstIntallTime", firstIntallTime);
            return true;
        }
        return DateUtils.isToday(firstIntallTime);
    }


    public long getFirstInstallTime() {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            //应用最后一次更新时间
            //long lastUpdateTime = packageInfo.lastUpdateTime;
            // LogUtil.debug("first install time : " + firstInstallTime + " last update time :" + lastUpdateTime);
            //应用装时间
            return packageInfo.firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /*获取所有广告对应的activity*/
    public List<Class> getAdActivity() {
        List<Class> list = new ArrayList<>();
        list.add(AdActivity.class);
//        list.add(TTLandingPageActivity.class);
//        list.add(TTPlayableLandingPageActivity.class);
//        list.add(TTVideoLandingPageActivity.class);
//        list.add(TTVideoLandingPageLink2Activity.class);
//        list.add(TTRewardVideoActivity.class);
//        list.add(TTRewardExpressVideoActivity.class);
//        list.add(TTFullScreenVideoActivity.class);
//        list.add(TTFullScreenExpressVideoActivity.class);
//        list.add(TTInterstitialActivity.class);
//        list.add(TTInterstitialExpressActivity.class);
//        list.add(TTDelegateActivity.class);
//        list.add(TTWebsiteActivity.class);
//        list.add(TTAppOpenAdActivity.class);
        list.add(FacebookActivity.class);
        list.add(CustomTabMainActivity.class);
//        list.add(AudienceNetworkActivity.class);
//        list.add(AppLovinFullscreenActivity.class);
//        list.add(AppLovinWebViewActivity.class);
//        list.add(MaxDebuggerActivity.class);
//        list.add(MaxDebuggerDetailActivity.class);
//        list.add(MaxDebuggerMultiAdActivity.class);
//        list.add(MaxDebuggerAdUnitsListActivity.class);
//        list.add(MaxDebuggerAdUnitDetailActivity.class);
//        list.add(MaxDebuggerTestLiveNetworkActivity.class);
        list.add(GoogleApiActivity.class);
//        list.add(AdUnitActivity.class);
//        list.add(AdUnitTransparentActivity.class);
//        list.add(AdUnitSoftwareActivity.class);
//        list.add(ApkConfirmDialogActivity.class);
//        list.add(TradplusGDPRAuthActivity.class);
        return list;
    }
}


