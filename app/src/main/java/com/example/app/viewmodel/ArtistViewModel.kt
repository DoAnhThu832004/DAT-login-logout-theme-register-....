package com.example.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiService
import com.example.app.model.response.Artist
import kotlinx.coroutines.launch

class ArtistViewModel(
    private val apiService: ApiService
): ViewModel() {
    private val _artistState = mutableStateOf(ArtistState())
    val artistState: State<ArtistState> = _artistState
    fun getArtists() {
        viewModelScope.launch {
            _artistState.value = _artistState.value.copy(isLoadingA = true, errorA = null)
            try {
                val response = apiService.getArtists()
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _artistState.value = _artistState.value.copy(
                        artists = body.result,
                        isLoadingA = false,
                        errorA = null
                    )
                } else {
                    _artistState.value = _artistState.value.copy(
                        isLoadingA = false,
                        errorA = "Error fetching artists: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _artistState.value = _artistState.value.copy(
                    isLoadingA = false,
                    errorA = e.localizedMessage ?: "Unknown error"
                )
            }
        }
    }
    data class ArtistState(
        val isLoadingA: Boolean = false,
        val artists: List<Artist>? = null,
        val errorA: String? = null
    )
}