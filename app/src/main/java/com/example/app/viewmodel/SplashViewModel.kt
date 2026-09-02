package com.example.app.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// SplashViewModel – [FIX #3] Tách business logic ra khỏi Composable
// Chịu trách nhiệm: đọc token, parse role, emit Destination để UI navigate
// Composable chỉ observe StateFlow, không chứa logic nghiệp vụ
// ─────────────────────────────────────────────────────────────────────────────
class SplashViewModel(
    private val sessionManager: SessionManager,
    private val loginViewModel: LoginViewModel,
    private val context: Context
) : ViewModel() {

    // Sealed class mô tả màn hình đích sau splash
    sealed class Destination {
        object Login : Destination()
        object Onboarding : Destination()           // Thêm: chưa xem onboarding lần nào
        data class Admin(val name: String) : Destination()
        data class User(val name: String) : Destination()
    }

    // StateFlow null = chưa xác định, non-null = đã sẵn sàng navigate
    private val _destination = MutableStateFlow<Destination?>(null)
    val destination: StateFlow<Destination?> = _destination.asStateFlow()

    /**
     * Chờ [delayMs] ms (cho animation hiển thị đủ), rồi resolve destination.
     *
     * Thứ tự ưu tiên:
     * 1. Nếu chưa xem onboarding → Onboarding
     * 2. Nếu không có token → Login
     * 3. Nếu có token → Admin/User theo role
     *
     * Gọi từ LaunchedEffect(Unit) trong Composable.
     */
    fun resolveDestination(delayMs: Long = SPLASH_TOTAL_DELAY_MS) {
        viewModelScope.launch {
            delay(delayMs)
            _destination.value = try {
                // Bước 1: kiểm tra onboarding (dùng cùng authDataStore)
                val onboardingKey = booleanPreferencesKey("onboarding_done")
                val hasSeenOnboarding = context.authDataStore.data.first()[onboardingKey] ?: false

                if (!hasSeenOnboarding) {
                    Destination.Onboarding
                } else {
                    // Bước 2: kiểm tra token đăng nhập
                    val token = sessionManager.getAccessToken()
                    if (token.isNullOrEmpty()) {
                        Destination.Login
                    } else {
                        when (loginViewModel.getRoleFromToken(token)) {
                            "ROLE_ADMIN" -> Destination.Admin("User")
                            "ROLE_USER"  -> Destination.User("User")
                            else         -> Destination.Login
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Destination.Login  // Fallback an toàn
            }
        }
    }

    companion object {
        // Thời gian hiển thị splash trước khi điều hướng (ms)
        const val SPLASH_TOTAL_DELAY_MS = 4500L
    }
}

