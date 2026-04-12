package com.example.app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.auth0.android.jwt.JWT
import com.example.app.model.ApiErrorUtils
import com.example.app.model.ApiService
import com.example.app.model.request.AuthenticationRequest
import com.example.app.model.response.ApiError
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState

    private var progressJob: Job? = null

    fun updateUsernameInput(input: String) {
        _loginUiState.value = _loginUiState.value.copy(
            usernameInput = input
        )
    }
    fun updatePassWordInput(input: String) {
        _loginUiState.value = _loginUiState.value.copy(
            passwordInput = input
        )
    }
    fun togglePasswordVisibility() {
        val currentState = _loginUiState.value
        _loginUiState.value = currentState.copy(isPasswordVisible = !currentState.isPasswordVisible)
    }
    fun login() {
        val username = _loginUiState.value.usernameInput
        val password = _loginUiState.value.passwordInput
        viewModelScope.launch {
            _loginUiState.value = _loginUiState.value.copy(isLoading = true, error = null)
            startSimulatedProgress()
            try {
                val response = apiService.authenticate(
                    AuthenticationRequest(
                        username = username,
                        password = password
                    ),
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.result?.authenticated == true && !body.result.token.isNullOrEmpty()) {
                        val token = body.result.token!!
                        val userId = getUserIdFromToken(token)
                        val role = getRoleFromToken(token)
                        sessionManager.saveSession(token)
                        sessionManager.saveUsername(username)

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
                            error = "Login successfully"
                        )
                    } else {
                        handleLoginFailure(body?.message ?: "Login failed")
                    }
                } else {
                    val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                    handleLoginFailure(apiErr?.message ?: "Registration failed")
                }
            } catch (e : Exception) {
                handleLoginFailure("Error: ${e.message}")
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
    fun resetLoginUiState(message : String) {
        viewModelScope.launch {
            _loginUiState.value = LoginUiState(isLoading = true, error = message)
            delay(1500)
            _loginUiState.value = _loginUiState.value.copy(isLoading = false,error = message)
        }
    }
    fun getRoleFromToken(token : String) : String? {
        return try {
            val jwt = JWT(token)
            val scope = jwt.getClaim("scope").asString()
            scope?.split(" ")?.firstOrNull()
        } catch (e : Exception) {
            null
        }
    }
    fun getUserIdFromToken(token : String): String? {
        return try {
            val jwt = JWT(token)
            jwt.getClaim("userId").asString()
        } catch (e : Exception) {
            null
        }
    }
    suspend fun logout() {
        sessionManager.clearSession()
        _loginUiState.value = LoginUiState()
    }
    fun logoutAndNavigate(onComplete: () -> Unit) {
        viewModelScope.launch {
            logout()  // suspend fun
            onComplete() // callback khi xong
        }
    }
    fun checkExistingSession(onLoggedIn: (String, String) -> Unit, onNotLoggedIn: () -> Unit) {
        viewModelScope.launch {
            val token = sessionManager.getAccessToken()
            if (!token.isNullOrEmpty()) {
                val role = getRoleFromToken(token)
                val name = "User" // Hoặc lấy từ token/API
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

        val name : String? = null,
        val token: String? = null,
        val userId : String? = null,
        val role: String? = null,
        val error: String? = null
    )
}
