package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiErrorUtils
import com.example.app.model.repository.UserRepository
import com.example.app.model.request.ChangePasswordRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val repository: UserRepository,
    private val loginViewModel: LoginViewModel,
    private val editProfileViewModel: EditProfileViewModel
) : ViewModel() {

    private val _state = MutableStateFlow(ChangePasswordState())
    val state: StateFlow<ChangePasswordState> = _state.asStateFlow()

    fun updateCurrentPassword(input: String) {
        _state.update { it.copy(currentPassword = input, currentPasswordError = null, generalError = null) }
    }

    fun updateNewPassword(input: String) {
        _state.update { it.copy(newPassword = input, newPasswordError = null, generalError = null) }
    }

    fun updateConfirmPassword(input: String) {
        _state.update { it.copy(confirmPassword = input, confirmPasswordError = null, generalError = null) }
    }

    fun resetSuccess() {
        _state.update { it.copy(isSuccess = false) }
    }

    fun changePassword() {
        val s = _state.value
        var currentErr: String? = null
        var newErr: String? = null
        var confirmErr: String? = null

        // ── Validate local ──
        when {
            s.currentPassword.isBlank() -> currentErr = "Vui lòng nhập mật khẩu hiện tại"
            s.currentPassword.length < 6 -> currentErr = "Mật khẩu tối thiểu 6 ký tự"
        }
        when {
            s.newPassword.isBlank() -> newErr = "Vui lòng nhập mật khẩu mới"
            s.newPassword.length < 8 -> newErr = "Mật khẩu mới tối thiểu 8 ký tự"
            s.newPassword.length > 100 -> newErr = "Mật khẩu tối đa 100 ký tự"
            s.newPassword == s.currentPassword -> newErr = "Mật khẩu mới phải khác mật khẩu cũ"
        }
        when {
            s.confirmPassword.isBlank() -> confirmErr = "Vui lòng xác nhận mật khẩu mới"
            s.confirmPassword != s.newPassword -> confirmErr = "Mật khẩu xác nhận không khớp"
        }

        if (currentErr != null || newErr != null || confirmErr != null) {
            _state.update {
                it.copy(
                    currentPasswordError = currentErr,
                    newPasswordError = newErr,
                    confirmPasswordError = confirmErr
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }
            try {
                val response = repository.changePassword(
                    ChangePasswordRequest(
                        oldPassword = s.currentPassword,
                        newPassword = s.newPassword,
                        confirmPassword = s.confirmPassword
                    )
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000) {
                        _state.update { it.copy(isLoading = false, isSuccess = true, generalError = null) }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                generalError = body?.result ?: "Đổi mật khẩu thất bại"
                            )
                        }
                    }
                } else {
                    val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                    val msg = when (apiErr?.code) {
                        1004, 1008, 1006 -> "Mật khẩu hiện tại không đúng"
                        else -> apiErr?.message ?: "Đổi mật khẩu thất bại (${response.code()})"
                    }
                    _state.update { it.copy(isLoading = false, generalError = msg) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        generalError = "Lỗi kết nối, vui lòng thử lại"
                    )
                }
            }
        }
    }

    data class ChangePasswordState(
        val currentPassword: String = "",
        val newPassword: String = "",
        val confirmPassword: String = "",
        // Field errors
        val currentPasswordError: String? = null,
        val newPasswordError: String? = null,
        val confirmPasswordError: String? = null,
        // General error (server / network)
        val generalError: String? = null,
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false
    )
}
