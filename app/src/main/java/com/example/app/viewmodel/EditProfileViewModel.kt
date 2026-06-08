package com.example.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiErrorUtils
import com.example.app.model.repository.UserRepository
import com.example.app.model.FileUtils
import com.example.app.model.request.UserUpdateRequest
import com.example.app.model.response.ApiError
import com.example.app.model.response.UserResponse
import com.example.app.model.response.UserResult
import com.example.app.model.response.RoleResult
import com.example.app.viewmodel.AlbumViewModel.AlbumState
import com.example.app.viewmodel.ArtistViewModel.ArtistState
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class EditProfileViewModel(
    private val repository: UserRepository,
    private val loginViewModel: LoginViewModel,
    private val sessionManager: SessionManager
): ViewModel() {
    private val _editUiState = MutableStateFlow(EditUiState())
    val editUiState: StateFlow<EditUiState> = _editUiState.asStateFlow()
    init {
        getMyInfo()
    }

    fun getMyInfo() {
        viewModelScope.launch {
            _editUiState.value = _editUiState.value.copy(isLoadingE = true, errorE = null)
            try {
                val response = repository.getUserInfo()
                if (response.isSuccessful) {
                    val body = response.body()
                    if(body?.code == 1000 && body != null) {
                        _editUiState.value = _editUiState.value.copy(
                            isLoadingE = false,
                            isSuccessfulE = false,
                            userResponse = body,
                            errorE = "get user successfully"
                        )
                        // Cập nhật lại userId và username vào sessionManager để dùng offline
                        body.result.id.let { sessionManager.saveUserId(it) }
                        body.result.username.let { sessionManager.saveUsername(it) }
                    } else {
                        tryLoadOfflineUser()
                    }
                } else {
                    tryLoadOfflineUser()
                }
            } catch (e : Exception) {
                tryLoadOfflineUser()
            }
        }
    }

    private suspend fun tryLoadOfflineUser() {
        val savedUserId = sessionManager.getSavedUserId()
        if (!savedUserId.isNullOrEmpty()) {
            val savedUsername = sessionManager.getSavedUsername()
            val token = sessionManager.getAccessToken()
            val role = if (!token.isNullOrEmpty()) loginViewModel.getRoleFromToken(token) else "USER"
            val roleName = role ?: "USER"
            val offlineUser = UserResponse(
                code = 1000,
                message = "Offline mode",
                result = UserResult(
                    id = savedUserId,
                    username = savedUsername ?: "Offline User",
                    firstName = "",
                    lastName = "",
                    dob = "",
                    imageUrl = "",
                    roles = listOf(RoleResult(roleName, "User role", emptyList())),
                    blocked = false
                )
            )
            _editUiState.value = _editUiState.value.copy(
                isLoadingE = false,
                isSuccessfulE = false,
                userResponse = offlineUser,
                errorE = "Offline mode active"
            )
        } else {
            resetEditUiState("Get user info failed (No internet and no cached session)")
        }
    }

    fun updateProfile(firstName: String, lastName: String, dob: String) {
        viewModelScope.launch {
            _editUiState.value = _editUiState.value.copy(isLoadingE = true, errorE = null)
            val userId = _editUiState.value.userResponse?.result?.id ?: loginViewModel.loginUiState.value.userId
            if (userId == null) {
                _editUiState.value = _editUiState.value.copy(isLoadingE = false, errorE = "User ID not found")
                return@launch
            }
            
            val currentUsername = _editUiState.value.userResponse?.result?.username ?: loginViewModel.loginUiState.value.name ?: ""
            // Lấy roles hiện tại để không bị mất quyền khi update
            val currentRoles = _editUiState.value.userResponse?.result?.roles?.map { it.name } ?: listOf("USER")

            try {
                val response = repository.updateUser(
                    id = userId,
                    UserUpdateRequest(
                        username = currentUsername,
                        password = null, // Backend nên xử lý nếu null thì không đổi pass
                        firstName = firstName,
                        lastName = lastName,
                        dob = dob,
                        roles = currentRoles
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
                        // Reset success state after a delay or let UI handle it
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

    fun clearSuccessState() {
        _editUiState.value = _editUiState.value.copy(isSuccessfulE = false)
    }

    fun resetEditUiState(message: String) {
        _editUiState.value = _editUiState.value.copy(isLoadingE = false, errorE = message)
        viewModelScope.launch {
            delay(3000)
            if (_editUiState.value.errorE == message) {
                _editUiState.value = _editUiState.value.copy(errorE = null)
            }
        }
    }
    // Trong EditProfileViewModel.kt

    fun uploadImage(userId: String, imageUri: Uri, context: Context) {
        viewModelScope.launch {
            _editUiState.value = _editUiState.value.copy(isLoadingE = true, errorE = null)
            try {
                // 1. Kiểm tra kỹ file
                val imageFile = FileUtils.getFileFromUri(context, imageUri)
                if (imageFile != null) {
                    // LƯU Ý QUAN TRỌNG:
                    // "image" ở đây phải trùng KHỚP hoàn toàn với @RequestParam("image") phía Backend.
                    // Nếu Backend dùng "file" hay "avatar", hãy sửa chữ "image" bên dưới thành từ đó.
                    val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)

                    val response = repository.uploadUserImage(userId, part)

                    if (response.isSuccessful) {
                        val body = response.body()
                        // Giả sử body trả về UserResponse mới nhất
                        if (body != null) {
                            _editUiState.value = _editUiState.value.copy(
                                isLoadingE = false,
                                isSuccessfulE = true,
                                userResponse = body, // Cập nhật user mới nhất từ phản hồi upload
                                errorE = "Upload successfully"
                            )
                            // GỌI LẠI hàm lấy thông tin để đảm bảo toàn bộ App được cập nhật (Optional nhưng khuyến khích)
                            getMyInfo()
                        } else {
                            // Trường hợp upload xong nhưng server không trả về body user mới
                            getMyInfo() // Buộc phải gọi lại để lấy URL ảnh mới
                        }
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Upload failed"
                        _editUiState.value = _editUiState.value.copy(
                            isLoadingE = false,
                            isSuccessfulE = false,
                            errorE = errorMsg
                        )
                    }
                } else {
                    _editUiState.value = _editUiState.value.copy(isLoadingE = false, errorE = "Cannot create file from URI")
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log lỗi để debug
                _editUiState.value = _editUiState.value.copy(isLoadingE = false, errorE = "Error: ${e.message}")
            }
        }
    }
    data class EditUiState(
        val isLoadingE: Boolean = false,
        val isSuccessfulE: Boolean = false,
        val errorE: String? = null,
        val userResponse: UserResponse? = null
    )
}