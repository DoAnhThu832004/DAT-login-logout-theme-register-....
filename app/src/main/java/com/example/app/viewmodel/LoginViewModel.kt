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


    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginUiState.value = _loginUiState.value.copy(isLoading = true, error = null)
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
                        resetLoginUiState(body?.message ?: "Login failed")
                    }
                } else {
                    val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                    resetLoginUiState(apiErr?.message ?: "Registration failed")
                }
            } catch (e : Exception) {
                resetLoginUiState("Error: ${e.message}")
            }
        }
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
    data class LoginUiState(
        val isLoading: Boolean = false,
        val isSuccessful: Boolean = false,
        val name : String? = null,
        val token: String? = null,
        val userId : String? = null,
        val role: String? = null,
        val error: String? = null
    )
}
