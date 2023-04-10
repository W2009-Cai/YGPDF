package com.yg.pdf

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.yg.pdf.ui.SplashActivity

class AppOpenManager(private val myApplication: MainApp) : Application.ActivityLifecycleCallbacks {
    private var activityStartCount = 0
    private var currentActivity: Activity? = null
    private var endTime: Long = 0


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
        activityStartCount++
        //数值从0变到1说明是从后台切到前台
        if (activityStartCount == 1) {
            if (System.currentTimeMillis() - endTime < 3000) {
                return
            }
            if (activity is MainActivity) {
                if (!activity.isShowAd) {
                    activity.startActivity(
                        Intent(
                            activity,
                            SplashActivity::class.java
                        )
                    )
                }
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
//        Adjust.onResume()
    }

    override fun onActivityPaused(activity: Activity) {
//        Adjust.onPause()
    }

    override fun onActivityStopped(activity: Activity) {
        activityStartCount--
        //数值从1到0说明是从前台切到后台
        if (activityStartCount == 0) {
            endTime = System.currentTimeMillis()
//            Logger.e("app 切到后台")
            //从前台切到后台  如果广告已展示  则重新请求
//            loadSplashAd(activity)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }


    companion object {
        private const val LOG_TAG = "AppOpenManager"
        private var isShowingAd = false
    }

    /**
     * Constructor
     */
    init {
        myApplication.registerActivityLifecycleCallbacks(this)
    }
}