package com.example.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiErrorUtils
import com.example.app.model.ApiService
import com.example.app.model.FileUtils
import com.example.app.model.request.UserUpdateRequest
import com.example.app.model.response.ApiError
import com.example.app.model.response.UserResponse
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
    private val apiService: ApiService,
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

    fun updateProfile(username: String, password: String?, firstName: String, lastName: String, dob: String) {
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

                    val response = apiService.uploadUserImage(userId, part)

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