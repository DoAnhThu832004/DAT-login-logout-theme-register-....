package com.example.app.theme.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Tận dụng hoặc mở rộng Preferences DataStore riêng cho Theme
val Context.dynamicThemeDataStore: DataStore<Preferences> by preferencesDataStore(name = "dynamic_theme_prefs")

/**
 * Local Data Source quản lý lưu trữ và cache cấu hình Theme dưới máy khách qua DataStore.
 * Giúp app khởi động tức thì với Theme đã lưu, không bị giật/flash trắng khi mở app.
 */
class LocalThemeDataSource(
    private val context: Context
) {
    companion object {
        val KEY_CACHED_THEME_JSON = stringPreferencesKey("cached_remote_theme_json")
        val KEY_DARK_MODE = booleanPreferencesKey("is_dark_theme_user_pref")
        val KEY_SEASONAL_OVERRIDE = stringPreferencesKey("seasonal_theme_override")
    }

    /**
     * Flow JSON cấu hình theme được cache.
     */
    val cachedThemeJsonFlow: Flow<String?> = context.dynamicThemeDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            prefs[KEY_CACHED_THEME_JSON]
        }

    /**
     * Flow chế độ Dark mode do người dùng chọn. Mặc định là true (Dark).
     */
    val isDarkModeFlow: Flow<Boolean> = context.dynamicThemeDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            prefs[KEY_DARK_MODE] ?: true
        }

    /**
     * Lưu JSON cấu hình Remote Config mới nhất vào DataStore.
     */
    suspend fun saveThemeJson(json: String) {
        context.dynamicThemeDataStore.edit { prefs ->
            prefs[KEY_CACHED_THEME_JSON] = json
        }
    }

    /**
     * Lưu cài đặt Dark Mode của người dùng.
     */
    suspend fun saveDarkMode(isDark: Boolean) {
        context.dynamicThemeDataStore.edit { prefs ->
            prefs[KEY_DARK_MODE] = isDark
        }
    }

    /**
     * Lưu override cho Seasonal Event (null = tự động theo Remote Config, true = luôn bật, false = tắt).
     */
    suspend fun saveSeasonalOverride(override: Boolean?) {
        context.dynamicThemeDataStore.edit { prefs ->
            if (override == null) {
                prefs.remove(KEY_SEASONAL_OVERRIDE)
            } else {
                prefs[KEY_SEASONAL_OVERRIDE] = override.toString()
            }
        }
    }

    val seasonalOverrideFlow: Flow<Boolean?> = context.dynamicThemeDataStore.data
        .map { prefs ->
            prefs[KEY_SEASONAL_OVERRIDE]?.toBooleanStrictOrNull()
        }
}
