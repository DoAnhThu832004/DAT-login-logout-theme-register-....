package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiErrorUtils
import com.example.app.model.ApiService
import com.example.app.model.request.UserUpdateRequest
import com.example.app.model.response.ApiError
import com.example.app.model.response.UserResponse
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val apiService: ApiService,
    private val loginViewModel: LoginViewModel,
    private val sessionManager: SessionManager
): ViewModel() {
    private val _editUiState = MutableStateFlow(EditUiState())
    val editUiState = _editUiState.asStateFlow()
    init {
        getMyInfo()
    }

    fun getMyInfo() {
        viewModelScope.launch {
            _editUiState.value = _editUiState.value.copy(isLoadingE = true, errorE = null)
            try {
                val response = apiService.getUserInfo()
                if (response.isSuccessful) {
                    val body = response.body()
                    if(body?.code == 1000 && body != null) {
                        _editUiState.value = _editUiState.value.copy(
                            isLoadingE = false,
                            isSuccessfulE = false,
                            userResponse = body,
                            errorE = "get user successfully"
                        )
                    } else {
                        resetEditUiState(body?.message ?: "Get failed")
                    }
                } else {
                    val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                    resetEditUiState(apiErr?.message ?: "Get failed")
                }
            } catch (e : Exception) {
                resetEditUiState("Error: ${e.message}")
            }
        }
    }

    fun updateProfile(username: String, password: String, firstName: String, lastName: String, dob: String) {
        viewModelScope.launch {
            _editUiState.value = _editUiState.value.copy(isLoadingE = true, errorE = null)
            val userId = loginViewModel.loginUiState.value.userId ?: run { _editUiState.value = _editUiState.value.copy(isLoadingE = false, errorE = "User ID not found")
                return@launch
            }
            try {
                val response = apiService.updateUser(
                    id = userId,
                    UserUpdateRequest(
                        username = username,
                        password = password,
                        firstName = firstName,
                        lastName = lastName,
                        dob = dob
                    )
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if(body?.code == 1000 && body != null) {
                        _editUiState.value = _editUiState.value.copy(
                            isLoadingE = false,
                            isSuccessfulE = true,
                            userResponse = body,
                            errorE = "Update successfully"
                        )
                    } else {
                        resetEditUiState(body?.message ?: "Update failed")
                    }
                } else {
                    val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                    resetEditUiState(apiErr?.message ?: "Update failed")
                }
            } catch (e : Exception) {
                resetEditUiState("Error: ${e.message}")
            }
        }
    }

    fun resetEditUiState(message: String) {
        viewModelScope.launch {
            _editUiState.value = _editUiState.value.copy(isLoadingE = true, errorE = message)
            delay(1500)
            _editUiState.value = _editUiState.value.copy(isLoadingE = false,isSuccessfulE = false, errorE = message)
        }
    }
    data class EditUiState(
        val isLoadingE: Boolean = false,
        val isSuccessfulE: Boolean = false,
        val errorE: String? = null,
        val userResponse: UserResponse? = null
    )
}