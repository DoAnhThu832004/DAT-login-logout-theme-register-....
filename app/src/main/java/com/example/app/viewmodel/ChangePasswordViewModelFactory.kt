package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app.model.repository.UserRepository

class ChangePasswordViewModelFactory(
    private val repository: UserRepository,
    private val loginViewModel: LoginViewModel,
    private val editProfileViewModel: EditProfileViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChangePasswordViewModel(repository, loginViewModel, editProfileViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
