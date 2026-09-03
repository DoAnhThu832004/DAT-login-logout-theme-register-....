package com.example.app.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.app.theme.domain.model.AppTheme
import com.example.app.theme.domain.repository.ThemeRepository
import com.example.app.theme.domain.usecase.FetchRemoteThemeUseCase
import com.example.app.theme.domain.usecase.GetAppThemeUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý State của Dynamic Theme, sẵn sàng inject qua Hilt hoặc khởi tạo qua Factory.
 */
class DynamicThemeViewModel(
    private val getAppThemeUseCase: GetAppThemeUseCase,
    private val fetchRemoteThemeUseCase: FetchRemoteThemeUseCase,
    private val themeRepository: ThemeRepository
) : ViewModel() {

    /**
     * StateFlow cung cấp AppTheme hiện tại cho toàn bộ UI (Recomposition tự động khi có thay đổi).
     */
    val themeState: StateFlow<AppTheme> = getAppThemeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.DefaultDarkTheme
        )

    /**
     * Tải lại theme thủ công từ Firebase Remote Config.
     */
    fun refreshTheme() {
        viewModelScope.launch {
            fetchRemoteThemeUseCase()
        }
    }

    /**
     * Thay đổi chế độ Dark/Light mode.
     */
    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            themeRepository.setDarkMode(isDark)
        }
    }

    /**
     * Bật / Tắt Seasonal Event bằng tay (dành cho người dùng muốn chuyển đổi).
     */
    fun setSeasonalThemeOverride(enabled: Boolean?) {
        viewModelScope.launch {
            themeRepository.setSeasonalThemeOverride(enabled)
        }
    }
}

/**
 * Factory khởi tạo DynamicThemeViewModel khi chưa dùng Hilt.
 */
class DynamicThemeViewModelFactory(
    private val themeRepository: ThemeRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DynamicThemeViewModel::class.java)) {
            val getAppThemeUseCase = GetAppThemeUseCase(themeRepository)
            val fetchRemoteThemeUseCase = FetchRemoteThemeUseCase(themeRepository)
            return DynamicThemeViewModel(getAppThemeUseCase, fetchRemoteThemeUseCase, themeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
