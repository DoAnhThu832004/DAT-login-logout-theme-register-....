package com.example.app.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.app.model.ApiService
import com.example.app.model.response.Playlist
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val apiService: ApiService
):ViewModel() {
    private val _playlistState = mutableStateOf(PlaylistState())
    val playlistState: State<PlaylistState> = _playlistState

    fun getPlaylists() {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response = apiService.getPlaylists()
                val body = response.body()
                if(response.isSuccessful && body != null) {
                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        playlists = body.result,
                        error = null
                    )
                } else {
                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        error = "Failed to load playlists"
                    )
                }
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    data class PlaylistState(
        val playlists: List<Playlist>? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}