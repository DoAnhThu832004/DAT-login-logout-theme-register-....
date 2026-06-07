package com.example.app.model.repository

import com.example.app.model.ApiService
import com.example.app.model.request.ChangePasswordRequest
import com.example.app.model.request.AuthenticationRequest
import com.example.app.model.request.UserCreationRequest
import com.example.app.model.request.UserUpdateRequest
import okhttp3.MultipartBody

class UserRepository(
    private val apiService: ApiService
) {
    suspend fun authenticate(request: AuthenticationRequest) = apiService.authenticate(request)
    suspend fun createUser(request: UserCreationRequest) = apiService.createUser(request)
    suspend fun updateUser(id: String, request: UserUpdateRequest) = apiService.updateUser(id, request)
    suspend fun getUserInfo() = apiService.getUserInfo()
    suspend fun uploadUserImage(id: String, imagePart: MultipartBody.Part) = apiService.uploadUserImage(id, imagePart)
    suspend fun changePassword(request: ChangePasswordRequest) = apiService.changePassword(request)

    suspend fun getUsers(page: Int = 1, size: Int = 20) = apiService.getUsers(page, size)
    suspend fun blockUser(userId: String) = apiService.blockUser(userId)
    suspend fun unblockUser(userId: String) = apiService.unblockUser(userId)
}
