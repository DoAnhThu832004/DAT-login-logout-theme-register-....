package com.example.app.theme.domain.repository

import com.example.app.theme.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow

/**
 * Interface Repository quản lý Theme của ứng dụng (Domain Layer).
 */
interface ThemeRepository {
    /**
     * Luồng phát sinh Theme hiện tại (kết hợp giữa Remote Config Realtime, Cache DataStore, và Default Theme).
     */
    fun getThemeFlow(): Flow<AppTheme>

    /**
     * Thực hiện tải và kích hoạt theme từ Firebase Remote Config, lưu vào cache DataStore.
     */
    suspend fun fetchAndSaveRemoteTheme(): Result<AppTheme>

    /**
     * Lấy theme mới nhất từ Cache mà không block luồng UI.
     */
    suspend fun getLatestCachedTheme(): AppTheme

    /**
     * Bật/Tắt chế độ Dark Mode của hệ thống/người dùng.
     */
    suspend fun setDarkMode(isDark: Boolean)

    /**
     * Bật/Tắt sự kiện Seasonal Theme từ người dùng (nếu cho phép toggle thủ công).
     */
    suspend fun setSeasonalThemeOverride(enabled: Boolean?)
}
