package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app.model.ApiService

class ArtistViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArtistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ArtistViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}