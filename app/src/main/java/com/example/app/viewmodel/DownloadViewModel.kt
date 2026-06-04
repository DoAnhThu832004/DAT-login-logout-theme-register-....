package com.example.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.repository.DownloadRepository
import com.example.app.model.response.Song
import com.example.app.model.room.DownloadedSongEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DownloadViewModel(private val downloadRepository: DownloadRepository) : ViewModel() {

    private val _isDownloaded = mutableStateOf(false)
    val isDownloaded: State<Boolean> = _isDownloaded

    private val _isDownloading = mutableStateOf(false)
    val isDownloading: State<Boolean> = _isDownloading

    private val _downloadError = mutableStateOf<String?>(null)
    val downloadError: State<String?> = _downloadError

    private val _downloadedSongs = MutableStateFlow<List<DownloadedSongEntity>>(emptyList())
    val downloadedSongs: StateFlow<List<DownloadedSongEntity>> = _downloadedSongs.asStateFlow()

    private var currentSongsJob: Job? = null

    /**
     * Tải danh sách bài hát của người dùng cụ thể. 
     * Nên gọi hàm này mỗi khi vào màn hình Download hoặc khi userId thay đổi.
     */
    fun loadDownloadedSongs(userId: String) {
        currentSongsJob?.cancel()
        currentSongsJob = viewModelScope.launch {
            downloadRepository.getUserDownloadedSongs(userId).collect {
                _downloadedSongs.value = it
            }
        }
    }

    fun checkDownloaded(songId: String, userId: String) {
        viewModelScope.launch {
            _isDownloaded.value = downloadRepository.checkDownloaded(songId, userId)
        }
    }

    fun downloadSong(song: Song, userId: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isDownloading.value = true
            _downloadError.value = null
            val success = downloadRepository.downloadSong(song, userId)
            _isDownloaded.value = success
            _isDownloading.value = false
            if (!success) {
                _downloadError.value = "Download failed. Please try again."
            }
            onComplete(success)
        }
    }

    fun deleteDownload(songId: String, userId: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = downloadRepository.deleteDownload(songId, userId)
            if (success) {
                _isDownloaded.value = false
            }
            onComplete(success)
        }
    }
}
