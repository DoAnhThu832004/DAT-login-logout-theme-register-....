package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiErrorUtils
import com.example.app.model.ApiService
import com.example.app.model.request.UserCreationRequest
import com.example.app.model.response.ApiError
import com.example.app.viewmodel.LoginViewModel.LoginUiState
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val apiService: ApiService
) : ViewModel() {
    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    fun register(username: String, password: String,firstName: String, lastName: String, dob: String) {
        viewModelScope.launch {
            _registerUiState.value = _registerUiState.value.copy(isLoading = true, error = null)
            try {
                val response = apiService.createUser(
                    UserCreationRequest(
                        username = username,
                        password = password,
                        firstName = firstName,
                        lastName = lastName,
                        dob = dob
                    )
                )
                if(response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.code == 1000) {
                        _registerUiState.value = _registerUiState.value.copy(
                            isLoading = false,
                            isSuccessful = true,
                            error = "Register successfully"
                        )
                    } else {
                        resetRegisterUiState(response.message())
                    }
                } else {
                    val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                    resetRegisterUiState(apiErr?.message ?: "Registration failed")
                }
            } catch (e : Exception) {
                resetRegisterUiState("Error: ${e.message}")
            }
        }
    }
    fun resetRegisterUiState(message : String) {
        viewModelScope.launch {
            _registerUiState.value = _registerUiState.value.copy(isLoading = true, error = message)
            delay(1500)
            _registerUiState.value = _registerUiState.value.copy(isLoading = false,error = message)
        }
    }
    data class RegisterUiState(
        val isLoading: Boolean = false,
        val isSuccessful: Boolean = false,
        val error: String? = null
    )
}