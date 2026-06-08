package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app.model.repository.SongRepository

class FavoriteViewModelFactory(
    private val songRepository: SongRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteViewModel(songRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
