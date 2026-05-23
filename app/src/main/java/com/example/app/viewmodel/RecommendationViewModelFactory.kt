package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app.model.repository.SongRepository

class RecommendationViewModelFactory(
    private val repository: SongRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecommendationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecommendationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
