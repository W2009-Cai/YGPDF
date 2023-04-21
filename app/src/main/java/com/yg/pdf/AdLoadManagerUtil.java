package com.yg.pdf;

public class AdLoadManagerUtil {
    private static double defAdValue = 0.01;//默认判断累计ecpm大于等于上报的值

    private static class Holder {
        private static final AdLoadManagerUtil instance = new AdLoadManagerUtil();
    }

    public static AdLoadManagerUtil getInstance() {
        return Holder.instance;
    }

    private AdLoadManagerUtil() {
    }

    public void calculateEcpm(Double currentEcpm) {
//        try {
//            double revenue = currentEcpm / 1000;
//            Bundle bundle = new Bundle();
//            bundle.putDouble(FirebaseAnalytics.Param.VALUE, revenue);
//            bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
//            AnalyticsUtils.INSTANCE.logEvent("Ads_Show_Revenue", bundle);
//            LogUtils.d("TAG1", "Total_Ads_Revenue事件上报 currentEcpm：" + revenue);
//            Map<String, Object> mapParam = new HashMap<String, Object>();
//            mapParam.put(AFInAppEventParameterName.CURRENCY, "USD");
//            mapParam.put(AFInAppEventParameterName.REVENUE, revenue);
//            AppsFlyerLib.getInstance().logEvent(MyApplication.getContext(), "Ads_Show_Revenue", mapParam);
//            AppsFlyerLib.getInstance().sendAdRevenue(MyApplication.getContext(), mapParam);
//            double valueTotal = Double.parseDouble(SPUtilsApp.getString("adValue", "0"));
//            valueTotal = valueTotal + currentEcpm / 1000;
//            if (valueTotal >= defAdValue) {
//                bundle = new Bundle();
//                bundle.putDouble(FirebaseAnalytics.Param.VALUE, valueTotal);
//                bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
//                AnalyticsUtils.INSTANCE.logEvent("Ads_Show_Revenue_001", bundle);
//                LogUtils.d("TAG1", "Total_Ads_Revenue_001事件上报 calculateEcpm：" + revenue);
//                mapParam = new HashMap<String, Object>();
//                mapParam.put(AFInAppEventParameterName.CURRENCY, "USD");
//                mapParam.put(AFInAppEventParameterName.REVENUE, valueTotal);
//                AppsFlyerLib.getInstance().logEvent(MyApplication.getContext(), "Ads_Show_Revenue_001", mapParam);
//                valueTotal = 0;
//            }
//            SPUtilsApp.putString("adValue", String.valueOf(valueTotal));
//            LogUtils.d("TAG1", "calculateEcpm: " + String.valueOf(valueTotal));
//        } catch (NumberFormatException e) {
//        }
    }

}
