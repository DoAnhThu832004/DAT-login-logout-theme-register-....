package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiService
import com.example.app.model.response.Album
import com.example.app.model.response.Artist
import com.example.app.model.response.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val retrofitService: ApiService
):ViewModel() {
    private val _sSong = MutableStateFlow<List<Song>>(emptyList())
    val sSong = _sSong.asStateFlow()
    private val _sAlbum = MutableStateFlow<List<Album>>(emptyList())
    val sAlbum = _sAlbum.asStateFlow()
    private var searchJob: Job? = null
    private val _sArtist = MutableStateFlow<List<Artist>>(emptyList())
    val sArtist = _sArtist.asStateFlow()

    fun onQueryChanged(query: String) {
        searchJob?.cancel()

        if(query.isBlank()) {
            clearSuggestions()
            return
        }
        searchJob = viewModelScope.launch {
            delay(500)
            performSearch(query)
        }
    }
    private suspend fun performSearch(name: String) {
        supervisorScope {
            val songDeferred = async {
                try {
                    val response = retrofitService.searchSongs(name)
                    if (response.isSuccessful) {
                        response.body()?.result ?: emptyList()
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            }
            val albumDeferred = async {
                try {
                    val response = retrofitService.searchAlbums(name)
                    if (response.isSuccessful) {
                        response.body()?.result ?: emptyList()
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            }
            val artistDeferred = async {
                try {
                    val response = retrofitService.searchArtists(name)
                    if (response.isSuccessful) {
                        response.body()?.result ?: emptyList()
                    } else {
                        emptyList()
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            }
            _sSong.value = songDeferred.await()
            _sAlbum.value = albumDeferred.await()
            _sArtist.value = artistDeferred.await()
        }
    }
    fun clearSuggestions() {
        searchJob?.cancel()
        _sSong.value = emptyList()
        _sAlbum.value = emptyList()
        _sArtist.value = emptyList()
    }
}