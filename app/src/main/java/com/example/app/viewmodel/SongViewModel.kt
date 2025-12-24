package com.example.app.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.app.model.response.Song
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiErrorUtils
import com.example.app.model.ApiService
import kotlinx.coroutines.launch

class SongViewModel(
    private val apiService: ApiService,
): ViewModel() {
    private val _songUiState = mutableStateOf(SongState())
    val songState: State<SongState> = _songUiState

    fun getSongs() {
        viewModelScope.launch {
            _songUiState.value = _songUiState.value.copy(isLoading = true, error = null)
            try {
                val response = apiService.getSongs()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            songs = body.result,
                            error = null
                        )
                    } else {
                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            error = "Failed to load songs"
                        )
                    }
                } else {
                    val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                    _songUiState.value = _songUiState.value.copy(
                        isLoading = false,
                        error = apiErr?.message ?: "Failed to load songs"
                    )
                }
            } catch (e: Exception) {
                _songUiState.value = _songUiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    fun refreshSongs() {
        getSongs()
    }
    data class SongState(
        val songs: List<Song>? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}