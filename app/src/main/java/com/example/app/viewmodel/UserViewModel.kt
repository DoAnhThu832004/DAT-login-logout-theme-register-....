package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.repository.UserRepository
import com.example.app.model.response.ApiResponse
import com.example.app.model.response.PageResponse
import com.example.app.model.response.UserResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {
    private val _userUiState = MutableStateFlow(UserUiState())
    val userUiState: StateFlow<UserUiState> = _userUiState.asStateFlow()

    fun getUsers() {
        viewModelScope.launch {
            _userUiState.value = _userUiState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.getUsers(page = 1, size = 100)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    val userList = body.result.result 

                    _userUiState.value = _userUiState.value.copy(
                        isLoading = false,
                        users = userList,
                        error = null
                    )
                } else {
                    _userUiState.value = _userUiState.value.copy(
                        isLoading = false,
                        error = "Failed to load users"
                    )
                }
            } catch (e: Exception) {
                _userUiState.value = _userUiState.value.copy(
                    isLoading = false,
                    error = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }

    fun toggleUserBlockStatus(user: UserResult) {
        viewModelScope.launch {
            // Không set isLoading = true để tránh UI flicker
            try {
                val newBlockedStatus = !user.blocked
                val response = if (user.blocked) {
                    repository.unblockUser(user.id)
                } else {
                    repository.blockUser(user.id)
                }

                // Kiểm tra HTTP success (2xx) — server lưu đúng dù response.result.blocked có thể sai
                if (response.isSuccessful) {
                    // Cập nhật trạng thái cục bộ ngay lập tức bằng logic !user.blocked
                    val currentUsers = _userUiState.value.users ?: emptyList()
                    val updatedList = currentUsers.map {
                        if (it.id == user.id) {
                            it.copy(blocked = newBlockedStatus)
                        } else it
                    }
                    _userUiState.value = _userUiState.value.copy(
                        users = updatedList,
                        error = null
                    )

                    // Đồng bộ im lặng với server (không set isLoading = true)
                    refreshUsersSilently()
                } else {
                    _userUiState.value = _userUiState.value.copy(
                        error = "Failed to update user status"
                    )
                }
            } catch (e: Exception) {
                _userUiState.value = _userUiState.value.copy(
                    error = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }

    /** Refresh danh sách người dùng mà không trigger isLoading (để tránh flicker UI) */
    private suspend fun refreshUsersSilently() {
        try {
            val response = repository.getUsers(page = 1, size = 100)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                _userUiState.value = _userUiState.value.copy(
                    users = body.result.result,
                    error = null
                )
            }
        } catch (_: Exception) {
            // Bỏ qua lỗi refresh im lặng — UI đã hiển thị trạng thái local
        }
    }

    data class UserUiState(
        val users: List<UserResult>? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
