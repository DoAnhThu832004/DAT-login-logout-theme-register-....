package com.example.app.theme.data.repository

import android.util.Log
import com.example.app.theme.data.datasource.LocalThemeDataSource
import com.example.app.theme.data.datasource.RemoteThemeDataSource
import com.example.app.theme.data.model.RemoteThemeDto
import com.example.app.theme.domain.model.AppTheme
import com.example.app.theme.domain.repository.ThemeRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Implementation chuẩn Clean Architecture cho [ThemeRepository].
 * Phối hợp an toàn giữa DataStore Local Cache, Firebase Remote Config Realtime, và Fallback Presets.
 */
class ThemeRepositoryImpl(
    private val localDataSource: LocalThemeDataSource,
    private val remoteDataSource: RemoteThemeDataSource,
    private val gson: Gson = Gson(),
    private val currentDateProvider: () -> LocalDate = { LocalDate.now() },
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : ThemeRepository {

    companion object {
        private const val TAG = "ThemeRepositoryImpl"
    }

    init {
        // Tự động lắng nghe cập nhật realtime từ Firebase Remote Config và lưu vào Local Cache DataStore
        scope.launch {
            try {
                remoteDataSource.listenToRealtimeUpdates().collect { newJson ->
                    if (newJson.isNotBlank()) {
                        Log.d(TAG, "Received realtime theme update from Firebase, saving to DataStore cache.")
                        localDataSource.saveThemeJson(newJson)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error in realtime updates listener: ${e.message}")
            }
        }
    }

    /**
     * Luồng Theme động cho UI.
     * Kết hợp giữa:
     * 1. Cài đặt Dark/Light của User
     * 2. Trạng thái bật/tắt Seasonal Theme Override
     * 3. JSON Theme được cache trong DataStore (cập nhật liên tục bởi Firebase)
     */
    override fun getThemeFlow(): Flow<AppTheme> {
        return combine(
            localDataSource.isDarkModeFlow,
            localDataSource.seasonalOverrideFlow,
            localDataSource.cachedThemeJsonFlow
        ) { isDark, seasonalOverride, cachedJson ->
            parseTheme(
                json = cachedJson,
                isDark = isDark,
                seasonalOverride = seasonalOverride
            )
        }
        .onStart {
            // Khi bắt đầu collect, kích hoạt fetch ngầm để luôn đảm bảo cache mới nhất
            scope.launch {
                fetchAndSaveRemoteTheme()
            }
        }
        .distinctUntilChanged()
    }

    /**
     * Tải JSON mới nhất từ Firebase Remote Config và lưu vào DataStore.
     */
    override suspend fun fetchAndSaveRemoteTheme(): Result<AppTheme> {
        return try {
            val json = remoteDataSource.fetchAndActivate()
            if (json.isNotBlank()) {
                localDataSource.saveThemeJson(json)
            }
            val isDark = localDataSource.isDarkModeFlow.first()
            val seasonalOverride = localDataSource.seasonalOverrideFlow.first()
            val theme = parseTheme(json, isDark, seasonalOverride)
            Result.success(theme)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch and save remote theme: ${e.message}", e)
            val isDark = localDataSource.isDarkModeFlow.first()
            val fallback = if (isDark) AppTheme.DefaultDarkTheme else AppTheme.DefaultLightTheme
            Result.failure(e)
        }
    }

    /**
     * Lấy theme hiện tại ngay lập tức mà không chờ flow.
     */
    override suspend fun getLatestCachedTheme(): AppTheme {
        val cachedJson = localDataSource.cachedThemeJsonFlow.first()
        val isDark = localDataSource.isDarkModeFlow.first()
        val seasonalOverride = localDataSource.seasonalOverrideFlow.first()
        return parseTheme(cachedJson, isDark, seasonalOverride)
    }

    override suspend fun setDarkMode(isDark: Boolean) {
        localDataSource.saveDarkMode(isDark)
    }

    override suspend fun setSeasonalThemeOverride(enabled: Boolean?) {
        localDataSource.saveSeasonalOverride(enabled)
    }

    /**
     * Parse JSON an toàn sang AppTheme:
     * - Bắt mọi lỗi JSON Malformed hoặc thiếu field.
     * - Kiểm tra hạn sử dụng (startDate / endDate).
     * - Tôn trọng seasonalOverride nếu user chỉ định.
     */
    fun parseTheme(
        json: String?,
        isDark: Boolean,
        seasonalOverride: Boolean?
    ): AppTheme {
        val defaultTheme = if (isDark) AppTheme.DefaultDarkTheme else AppTheme.DefaultLightTheme

        if (json.isNullOrBlank()) {
            return defaultTheme
        }

        return try {
            val dto = gson.fromJson(json, RemoteThemeDto::class.java)
            if (dto == null) {
                return defaultTheme
            }

            // Nếu người dùng cưỡng ép tắt seasonal theme
            if (seasonalOverride == false) {
                return defaultTheme
            }

            // Chuyển sang Domain AppTheme với kiểm tra ngày hết hạn
            val theme = dto.toDomain(
                defaultTheme = defaultTheme,
                currentDate = currentDateProvider()
            )

            // Nếu người dùng cưỡng ép bật seasonal theme bỏ qua kiểm tra ngày
            if (seasonalOverride == true) {
                theme.copy(isSeasonalEventActive = true)
            } else {
                theme
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Remote Config Theme JSON, falling back to default theme: ${e.message}")
            defaultTheme
        }
    }
}
