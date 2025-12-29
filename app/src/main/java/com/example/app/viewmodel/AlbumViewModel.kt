package com.example.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiErrorUtils
import com.example.app.model.ApiService
import com.example.app.model.request.AlbumCreationRequest
import com.example.app.model.request.AlbumUpdateRequest
import com.example.app.model.request.SongCreationRequest
import com.example.app.model.request.SongUpdateRequest
import com.example.app.model.response.Album
import kotlinx.coroutines.launch

class AlbumViewModel(
    private val apiService: ApiService
) : ViewModel() {
    private val _albumUiState = mutableStateOf(AlbumState())
    val albumState: State<AlbumState> = _albumUiState

    fun getAlbums() {
        viewModelScope.launch {
            _albumUiState.value = _albumUiState.value.copy(isLoading = true, error = null)
            try {
                val response = apiService.getAlbums()
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _albumUiState.value = _albumUiState.value.copy(
                        isLoading = false,
                        albums = body.result,
                        error = null
                    )
                } else {
                    _albumUiState.value = _albumUiState.value.copy(
                        isLoading = false,
                        error = "Failed to load albums"
                    )
                }
            } catch (e : Exception) {
                _albumUiState.value = _albumUiState.value.copy(isLoading = false)
            }
        }
    }
    fun createAlbum(name: String, description: String) {
        viewModelScope.launch {
            _albumUiState.value = _albumUiState.value.copy(
                isLoading = true,
                error = null,
                isCreating = true
            )
            try {
                val request = AlbumCreationRequest(
                    name = name,
                    description = description
                )
                val response = apiService.createAlbum(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        val currentAlbums = _albumUiState.value.albums?.toMutableList() ?: mutableListOf()
                        currentAlbums.add(body.result)
                        _albumUiState.value = _albumUiState.value.copy(
                            isLoading = false,
                            isCreating = false,
                            albums = currentAlbums,
                            error = null
                        )
                    } else {
                        _albumUiState.value = _albumUiState.value.copy(
                            isLoading = false,
                            isCreating = false,
                            error = "Failed to create album"
                        )
                    }
                }
            } catch (e: Exception) {
                _albumUiState.value = _albumUiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}",
                    isCreating = false
                )
            }
        }
    }
    fun deleteAlbum(id: String) {
        viewModelScope.launch {
            _albumUiState.value = _albumUiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response = apiService.deleteAlbum(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000) {
                        val currentAlbums = _albumUiState.value.albums ?: emptyList()
                        val updatedAlbums = currentAlbums.filter { it.id != id }
                        _albumUiState.value = _albumUiState.value.copy(
                            isLoading = false,
                            albums = updatedAlbums,
                            error = null
                        )
                    } else {
                        _albumUiState.value = _albumUiState.value.copy(
                            isLoading = false,
                            error = "Failed to delete album"
                        )
                    }
                }
            }catch (e: Exception) {
                _albumUiState.value = _albumUiState.value.copy(
                    isLoading = false,
                    error = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }
    fun updateAlbum(id: String, name: String, description: String, status: String) {
        viewModelScope.launch {
            _albumUiState.value = _albumUiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val request = AlbumUpdateRequest(
                    name = name,
                    description = description,
                    status = status
                )
                val response = apiService.updateAlbum(id, request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        val updateAlbumFromApi = body.result
                        val currentList = _albumUiState.value.albums ?: emptyList()
                        val updatedList = currentList.map {
                            if (it.id == id) {
                                updateAlbumFromApi
                            } else {
                                it
                            }
                        }
                        _albumUiState.value = _albumUiState.value.copy(
                            isLoading = false,
                            albums = updatedList,
                            error = null
                        )
                    }
                } else {
                    _albumUiState.value = _albumUiState.value.copy(
                        isLoading = false,
                        error = "Failed to update album"
                    )
                }
            } catch (e : Exception) {
                _albumUiState.value = _albumUiState.value.copy(isLoading = false)
            }
        }
    }
    data class AlbumState(
        val albums: List<Album>? = null,
        val isCreating: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}