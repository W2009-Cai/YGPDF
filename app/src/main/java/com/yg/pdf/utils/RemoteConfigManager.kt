package com.yg.pdf.utils

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.yg.pdf.R
import com.yg.pdf.bean.RemoteConfig


/**
 * firebase 远程配置
 */
object RemoteConfigManager {
    private var firebaseRemoteConfig: FirebaseRemoteConfig? = null
    private var mRemoteConfig: RemoteConfig? = null

    init {
        if (firebaseRemoteConfig == null) {
            firebaseRemoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 30 * 60
            }
            firebaseRemoteConfig?.setConfigSettingsAsync(configSettings)
            firebaseRemoteConfig?.setDefaultsAsync(R.xml.remote_config_defaults)
            firebaseRemoteConfig?.fetchAndActivate()
        }

    }

    fun init() {
        mRemoteConfig = GsonUtils.fromJson(firebaseRemoteConfig?.getString("Dynamic_config"), RemoteConfig::class.java)
    }

    /**设置默认值*/
    fun setDefaultsData(map: HashMap<String, Any>) {
        firebaseRemoteConfig?.setDefaultsAsync(map)
    }

    fun getmRemoteConfig(): RemoteConfig? {
        firebaseRemoteConfig?.fetchAndActivate()
        Log.d("TAG", "RemoteConfig: ${firebaseRemoteConfig?.getString("Dynamic_config")}")
        mRemoteConfig = GsonUtils.fromJson(firebaseRemoteConfig?.getString("Dynamic_config"), RemoteConfig::class.java)
        return mRemoteConfig
    }

    fun displayWelcomeMessage() {
        Log.d("TAG", "displayWelcomeMessage: ${firebaseRemoteConfig?.getString("Dynamic_config")}")
        Log.d("TAG", "displayWelcomeMessage: $mRemoteConfig")
        // [START get_config_values]
//        val welcomeMessage: String = remoteConfig.getString(WELCOME_MESSAGE_KEY)
        // [END get_config_values]
    }


}