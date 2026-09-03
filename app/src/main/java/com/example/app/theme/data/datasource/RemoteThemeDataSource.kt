package com.example.app.theme.data.datasource

import android.util.Log
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Remote Data Source tương tác trực tiếp với Firebase Remote Config.
 * Hỗ trợ cả Fetch/Activate truyền thống và Real-time Config Updates qua [addOnConfigUpdateListener].
 */
class RemoteThemeDataSource(
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
) {
    companion object {
        private const val TAG = "RemoteThemeDataSource"
        const val KEY_APP_THEME_CONFIG = "app_theme_config"

        // Default JSON mẫu cho Halloween được thiết lập sẵn trong Remote Config Defaults
        const val DEFAULT_HALLOWEEN_JSON = """
        {
          "themeId": "halloween_2026",
          "themeName": "Halloween Spooky Night",
          "colors": {
            "background": "#120826",
            "surface": "#1E0F3D",
            "surfaceVariant": "#2A1854",
            "primary": "#7C3AED",
            "secondary": "#A855F7",
            "accent": "#F97316",
            "accentSecondary": "#FACC15",
            "textPrimary": "#FFFFFF",
            "textSecondary": "#C4B5FD",
            "cardBackground": "#2A1854",
            "cardBorder": "#4C1D95",
            "gradientStart": "#7C3AED",
            "gradientEnd": "#DB2777"
          },
          "cornerRadiusDp": 20,
          "buttonCornerRadiusDp": 28,
          "chipCornerRadiusDp": 14,
          "bannerImageUrl": "",
          "seasonalIconUrl": "",
          "isSeasonalEventActive": true,
          "startDate": "2026-10-15",
          "endDate": "2026-11-02"
        }
        """
    }

    init {
        try {
            // Cấu hình fetch interval (0s trong debug để test realtime tức thì)
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
            remoteConfig.setConfigSettingsAsync(configSettings)

            // Khởi tạo giá trị mặc định cho key "app_theme_config"
            remoteConfig.setDefaultsAsync(
                mapOf(KEY_APP_THEME_CONFIG to DEFAULT_HALLOWEEN_JSON)
            )
        } catch (e: Exception) {
            Log.w(TAG, "Error initializing RemoteConfig settings: ${e.message}")
        }
    }

    /**
     * Lấy giá trị JSON hiện tại của theme từ Firebase Remote Config.
     */
    fun getCurrentThemeJson(): String {
        return try {
            remoteConfig.getString(KEY_APP_THEME_CONFIG)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading theme from RemoteConfig: ${e.message}")
            ""
        }
    }

    /**
     * Tải và kích hoạt cấu hình mới nhất từ server Firebase.
     */
    suspend fun fetchAndActivate(): String {
        return try {
            val updated = remoteConfig.fetchAndActivate().await()
            Log.d(TAG, "fetchAndActivate completed. Updated: $updated")
            getCurrentThemeJson()
        } catch (e: Exception) {
            Log.e(TAG, "Fetch remote config failed: ${e.message}")
            getCurrentThemeJson()
        }
    }

    /**
     * Lắng nghe cập nhật JSON Theme theo thời gian thực (Real-time Config Update).
     * Khi Admin thay đổi theme hoặc màu sắc trên Firebase Console và publish,
     * listener này sẽ nhận sự kiện ngay lập tức mà không cần restart ứng dụng.
     */
    fun listenToRealtimeUpdates(): Flow<String> = callbackFlow {
        // Emit giá trị hiện tại đầu tiên
        val initialJson = getCurrentThemeJson()
        if (initialJson.isNotBlank()) {
            trySend(initialJson)
        }

        val listenerRegistration = try {
            remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    Log.d(TAG, "Remote config updated keys: ${configUpdate.updatedKeys}")
                    if (configUpdate.updatedKeys.contains(KEY_APP_THEME_CONFIG)) {
                        remoteConfig.activate().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val newJson = getCurrentThemeJson()
                                Log.d(TAG, "New theme activated from realtime listener: $newJson")
                                trySend(newJson)
                            }
                        }
                    }
                }

                override fun onError(error: FirebaseRemoteConfigException) {
                    Log.w(TAG, "ConfigUpdateListener error: ${error.message}", error)
                }
            })
        } catch (e: Exception) {
            Log.w(TAG, "Realtime listener not supported or failed to register: ${e.message}")
            null
        }

        awaitClose {
            listenerRegistration?.remove()
        }
    }
}
