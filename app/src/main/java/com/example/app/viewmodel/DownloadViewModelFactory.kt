package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app.model.repository.DownloadRepository

class DownloadViewModelFactory(
    private val downloadRepository: DownloadRepository,
    private val sessionManager: SessionManager? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DownloadViewModel(downloadRepository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
