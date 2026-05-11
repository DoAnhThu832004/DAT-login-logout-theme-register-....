package com.example.app.model.repository

import com.example.app.model.ApiService
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
}
