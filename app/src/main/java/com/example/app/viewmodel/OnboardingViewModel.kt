package com.example.app.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// ── Key riêng cho onboarding (dùng authDataStore đã có sẵn) ─────────────────
private val ONBOARDING_DONE_KEY = booleanPreferencesKey("onboarding_done")

/**
 * ViewModel quản lý logic onboarding:
 * – Đọc/ghi trạng thái "đã xem" từ DataStore
 * – Không chứa UI state (state hoisting ở composable)
 */
class OnboardingViewModel(private val context: Context) : ViewModel() {

    /** Flow phát ra true nếu user đã xem onboarding, false nếu chưa */
    val hasSeenOnboarding: Flow<Boolean> = context.authDataStore.data.map { prefs ->
        prefs[ONBOARDING_DONE_KEY] ?: false
    }

    /** Gọi khi user hoàn tất onboarding (nhấn "Bắt đầu") */
    fun markOnboardingDone() {
        viewModelScope.launch {
            context.authDataStore.edit { prefs ->
                prefs[ONBOARDING_DONE_KEY] = true
            }
        }
    }
}

/** Factory pattern dùng chung với project (không dùng Hilt) */
class OnboardingViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OnboardingViewModel(context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
