package com.example.app.analytics

import android.util.Log
import com.example.app.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

/**
 * Singleton quản lý Cấu hình từ xa (Remote Config & Feature Flags)
 */
object RemoteConfigManager {
    private const val TAG = "RemoteConfigManager"

    // Định nghĩa các key cấu hình
    const val KEY_SHOW_ADS = "show_banner_ads"
    const val KEY_MAINTENANCE_MODE = "is_maintenance_mode"
    const val KEY_WELCOME_MESSAGE = "welcome_banner_text"

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance()
    }

    fun init(onComplete: (() -> Unit)? = null) {
        val interval = if (BuildConfig.DEBUG) 0L else 3600L
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(interval)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Thiết lập giá trị mặc định (Defaults)
        val defaultMap: Map<String, Any> = mapOf(
            KEY_SHOW_ADS to true,
            KEY_MAINTENANCE_MODE to false,
            KEY_WELCOME_MESSAGE to "Chào mừng bạn đến với ứng dụng!"
        )
        remoteConfig.setDefaultsAsync(defaultMap)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Remote config fetched and activated successfully.")
                } else {
                    Log.w(TAG, "Fetch remote config failed: ${task.exception?.message}")
                }
                onComplete?.invoke()
            }
    }

    fun isShowAds(): Boolean = remoteConfig.getBoolean(KEY_SHOW_ADS)
    fun isMaintenanceMode(): Boolean = remoteConfig.getBoolean(KEY_MAINTENANCE_MODE)
    fun getWelcomeMessage(): String = remoteConfig.getString(KEY_WELCOME_MESSAGE)
}
