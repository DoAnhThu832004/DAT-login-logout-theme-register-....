package com.example.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.repository.DownloadRepository
import com.example.app.model.response.Song
import kotlinx.coroutines.launch

class DownloadViewModel(private val downloadRepository: DownloadRepository) : ViewModel() {

    private val _isDownloaded = mutableStateOf(false)
    val isDownloaded: State<Boolean> = _isDownloaded

    private val _isDownloading = mutableStateOf(false)
    val isDownloading: State<Boolean> = _isDownloading

    private val _downloadError = mutableStateOf<String?>(null)
    val downloadError: State<String?> = _downloadError

    // Chỉ hiển thị bài hát mà người dùng CHỦ ĐỘNG tải xuống
    val downloadedSongs: kotlinx.coroutines.flow.Flow<List<com.example.app.model.room.DownloadedSongEntity>> = downloadRepository.getUserDownloadedSongs()

    fun checkDownloaded(songId: String) {
        viewModelScope.launch {
            _isDownloaded.value = downloadRepository.checkDownloaded(songId)
        }
    }

    fun downloadSong(song: Song, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isDownloading.value = true
            _downloadError.value = null
            val success = downloadRepository.downloadSong(song)
            _isDownloaded.value = success
            _isDownloading.value = false
            if (!success) {
                _downloadError.value = "Download failed. Please try again."
            }
            onComplete(success)
        }
    }

    fun deleteDownload(songId: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = downloadRepository.deleteDownload(songId)
            if (success) {
                _isDownloaded.value = false
            }
            onComplete(success)
        }
    }
}
