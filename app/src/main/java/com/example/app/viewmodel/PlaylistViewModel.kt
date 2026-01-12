package com.example.app.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.app.model.ApiService
import com.example.app.model.response.Playlist
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.example.app.model.request.AlbumCreationRequest
import com.example.app.model.request.PlaylistCreateRequest
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val apiService: ApiService
):ViewModel() {
    private val _playlistState = mutableStateOf(PlaylistState())
    val playlistState: State<PlaylistState> = _playlistState

    fun getMyPlaylists() {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response =  apiService.getMyPlaylists()
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
            }catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
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
    fun createPlaylist(name : String, description: String) {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(
                isLoading = true,
                error = null,
                isCreating = true
            )
            try {
                val request = PlaylistCreateRequest(
                    title = name,
                    description = description
                )
                val response = apiService.createPlaylist(request)
                if(response.isSuccessful) {
                    val body = response.body()
                    if(body?.code == 1000 && body.result != null) {
                        val currentPlaylists = _playlistState.value.playlists?.toMutableList() ?: mutableListOf()
                        currentPlaylists.add(body.result)
                        _playlistState.value = _playlistState.value.copy(
                            isLoading = false,
                            isCreating = false,
                            playlists = currentPlaylists,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}",
                    isCreating = false
                )
            }
        }
    }
    data class PlaylistState(
        val playlists: List<Playlist>? = null,
        val isCreating: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}