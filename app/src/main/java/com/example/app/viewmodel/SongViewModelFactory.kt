package com.example.app.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app.model.ApiService
import com.example.app.model.repository.SongRepository

class SongViewModelFactory(
    private val repository: SongRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SongViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SongViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}