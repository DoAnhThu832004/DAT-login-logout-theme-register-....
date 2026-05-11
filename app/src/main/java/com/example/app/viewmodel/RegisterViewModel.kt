package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiErrorUtils
import com.example.app.model.repository.UserRepository
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
    private val repository: UserRepository
) : ViewModel() {
    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    fun updateUsernameInput(input: String) {
       _registerUiState.value = _registerUiState.value.copy(
            usernameInput = input
        )
    }
    fun updatePasswordInput(input: String) {
        _registerUiState.value = _registerUiState.value.copy(
            passwordInput = input
        )
    }
    fun updateFirstNameInput(input: String) {
        _registerUiState.value = _registerUiState.value.copy(
            firstNameInput = input
        )
    }
    fun updateLastNameInput(input: String) {
        _registerUiState.value = _registerUiState.value.copy(
            lastNameInput = input
        )
    }
    fun updateDobInput(input: String) {
        _registerUiState.value = _registerUiState.value.copy(
            dobInput = input
        )
    }

    fun register() {
        val username = _registerUiState.value.usernameInput
        val password = _registerUiState.value.passwordInput
        val firstName = _registerUiState.value.firstNameInput
        val lastName = _registerUiState.value.lastNameInput
        val dob = _registerUiState.value.dobInput
        viewModelScope.launch {
            _registerUiState.value = _registerUiState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.createUser(
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
        var usernameInput: String = "",
        val passwordInput: String = "",
        val firstNameInput: String = "",
        val lastNameInput: String = "",
        val dobInput: String = "",
        val error: String? = null
    )
}