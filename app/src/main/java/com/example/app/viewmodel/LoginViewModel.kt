package com.example.app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.example.app.model.ApiErrorUtils
import com.example.app.model.repository.UserRepository
import com.example.app.model.request.AuthenticationRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState

    private var progressJob: Job? = null

    fun updateUsernameInput(input: String) {
        _loginUiState.value = _loginUiState.value.copy(
            usernameInput = input,
            usernameError = null,
            error = null
        )
    }

    fun updatePassWordInput(input: String) {
        _loginUiState.value = _loginUiState.value.copy(
            passwordInput = input,
            passwordError = null,
            error = null
        )
    }

    fun togglePasswordVisibility() {
        val currentState = _loginUiState.value
        _loginUiState.value = currentState.copy(isPasswordVisible = !currentState.isPasswordVisible)
    }

    fun login() {
        val username = _loginUiState.value.usernameInput.trim()
        val password = _loginUiState.value.passwordInput

        // ── Validate local trước khi gọi API ──
        var usernameErr: String? = null
        var passwordErr: String? = null

        when {
            username.isBlank() -> usernameErr = "Tên đăng nhập không được để trống"
            username.length > 50 -> usernameErr = "Tên đăng nhập tối đa 50 ký tự"
        }
        when {
            password.isBlank() -> passwordErr = "Mật khẩu không được để trống"
            password.length > 100 -> passwordErr = "Mật khẩu tối đa 100 ký tự"
        }

        if (usernameErr != null || passwordErr != null) {
            _loginUiState.value = _loginUiState.value.copy(
                usernameError = usernameErr,
                passwordError = passwordErr
            )
            return
        }

        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            com.example.app.analytics.AnalyticsHelper.logLoginAttempt("email")

            _loginUiState.value = _loginUiState.value.copy(
                isLoading = true,
                error = null,
                usernameError = null,
                passwordError = null
            )
            startSimulatedProgress()
            try {
                val response = repository.authenticate(
                    AuthenticationRequest(
                        username = username,
                        password = password
                    ),
                )
                val durationMs = System.currentTimeMillis() - startTime
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.result?.authenticated == true && !body.result.token.isNullOrEmpty()) {
                        val token = body.result.token!!
                        val userId = getUserIdFromToken(token)
                        val role = getRoleFromToken(token)
                        sessionManager.saveSession(token)
                        sessionManager.saveUsername(username)
                        // Lưu userId để dùng offline (DownloadScreen)
                        if (!userId.isNullOrEmpty()) {
                            sessionManager.saveUserId(userId)
                        }

                        // Telemetry: Login success & Set User Profile
                        com.example.app.analytics.AnalyticsHelper.logLoginSuccess("email", durationMs)
                        com.example.app.analytics.AnalyticsHelper.setUserProfile(userId, role, "free")
                        com.example.app.analytics.CrashlyticsHelper.setUserId(userId)

                        progressJob?.cancel()
                        _loginUiState.value = _loginUiState.value.copy(progress = 100f)
                        delay(300)

                        _loginUiState.value = _loginUiState.value.copy(
                            isLoading = false,
                            isSuccessful = true,
                            name = username,
                            token = token,
                            userId = userId,
                            role = role,
                            error = null
                        )
                    } else {
                        // Kiểm tra mã lỗi trong body ngay cả khi response thành công (một số backend trả về 200 OK kèm mã lỗi)
                        val errCode = body?.code?.toString() ?: "AUTH_FAILED"
                        val msg = when (body?.code) {
                            1011 -> "Tài khoản của bạn đã bị khóa bởi quản trị viên"
                            1006 -> "Tên đăng nhập hoặc mật khẩu không đúng"
                            else -> "Đăng nhập thất bại, vui lòng thử lại"
                        }
                        com.example.app.analytics.AnalyticsHelper.logLoginFailed("email", errCode, msg)
                        handleLoginFailure(msg)
                    }
                } else {
                    val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                    val errCode = apiErr?.code?.toString() ?: response.code().toString()
                    val msg = when (apiErr?.code) {
                        1006 -> "Tên đăng nhập hoặc mật khẩu không đúng"
                        1001 -> "Tài khoản không tồn tại"
                        1011 -> "Tài khoản của bạn đã bị khóa bởi quản trị viên"
                        else -> apiErr?.message ?: "Đăng nhập thất bại (${response.code()})"
                    }
                    com.example.app.analytics.AnalyticsHelper.logLoginFailed("email", errCode, msg)
                    handleLoginFailure(msg)
                }
            } catch (e: Exception) {
                com.example.app.analytics.AnalyticsHelper.logLoginFailed("email", "NETWORK_ERROR", e.message ?: "Network error")
                com.example.app.analytics.CrashlyticsHelper.recordException(e, "feature", "auth_login")
                handleLoginFailure("Lỗi kết nối, vui lòng kiểm tra mạng và thử lại")
            }
        }
    }

    private fun startSimulatedProgress() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            var currentProgress = 0f
            while (currentProgress < 95f) {
                delay(100)
                currentProgress += (95f - currentProgress) * 0.15f
                _loginUiState.value = _loginUiState.value.copy(progress = currentProgress)
            }
        }
    }

    private suspend fun handleLoginFailure(message: String) {
        progressJob?.cancel()
        _loginUiState.value = _loginUiState.value.copy(progress = 0f)
        resetLoginUiState(message)
    }

    fun resetLoginUiState(message: String) {
        viewModelScope.launch {
            _loginUiState.value = LoginUiState(isLoading = true, error = message)
            delay(1500)
            _loginUiState.value = _loginUiState.value.copy(isLoading = false, error = message)
        }
    }

    fun getRoleFromToken(token: String): String? {
        return try {
            val jwt = JWT(token)
            val scope = jwt.getClaim("scope").asString()
            scope?.split(" ")?.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    fun getUserIdFromToken(token: String): String? {
        return try {
            val jwt = JWT(token)
            jwt.getClaim("userId").asString()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun logout() {
        com.example.app.analytics.AnalyticsHelper.logLogout("profile_page")
        sessionManager.clearSession()
        _loginUiState.value = LoginUiState()
    }

    fun logoutAndNavigate(onComplete: () -> Unit) {
        viewModelScope.launch {
            logout()
            onComplete()
        }
    }

    fun checkExistingSession(onLoggedIn: (String, String) -> Unit, onNotLoggedIn: () -> Unit) {
        viewModelScope.launch {
            val token = sessionManager.getAccessToken()
            if (!token.isNullOrEmpty()) {
                val role = getRoleFromToken(token)
                val name = "User"
                if (role != null) {
                    onLoggedIn(name, role)
                } else onNotLoggedIn()
            } else {
                onNotLoggedIn()
            }
        }
    }

    data class LoginUiState(
        val isLoading: Boolean = false,
        val isSuccessful: Boolean = false,
        val progress: Float = 0f,

        var usernameInput: String = "",
        val passwordInput: String = "",
        val isPasswordVisible: Boolean = false,

        // Field-level errors
        val usernameError: String? = null,
        val passwordError: String? = null,

        val name: String? = null,
        val token: String? = null,
        val userId: String? = null,
        val role: String? = null,
        val error: String? = null
    )
}
