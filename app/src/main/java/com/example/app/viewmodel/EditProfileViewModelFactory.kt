package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app.model.ApiService

class EditProfileViewModelFactory(
    private val apiService: ApiService,
    private val loginViewModel: LoginViewModel,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(apiService,loginViewModel, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}