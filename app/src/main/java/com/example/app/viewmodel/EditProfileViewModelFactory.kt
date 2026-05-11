package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app.model.repository.UserRepository

class EditProfileViewModelFactory(
    private val repository: UserRepository,
    private val loginViewModel: LoginViewModel,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditProfileViewModel(repository, loginViewModel, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}