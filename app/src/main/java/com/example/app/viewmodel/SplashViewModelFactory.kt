package com.example.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// ─────────────────────────────────────────────────────────────────────────────
// Factory cho SplashViewModel – theo pattern thống nhất trong dự án
// Thêm context để SplashViewModel đọc DataStore (onboarding + authDataStore)
// ─────────────────────────────────────────────────────────────────────────────
class SplashViewModelFactory(
    private val sessionManager: SessionManager,
    private val loginViewModel: LoginViewModel,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(sessionManager, loginViewModel, context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
