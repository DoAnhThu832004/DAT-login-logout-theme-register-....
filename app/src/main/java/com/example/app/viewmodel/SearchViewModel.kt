package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.repository.SearchRepository
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
    private val repository: SearchRepository
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
                    val response = repository.searchSongs(name)
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
                    val response = repository.searchAlbums(name)
                    if (response.isSuccessful) {
                        response.body()?.result?.result ?: emptyList()
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
                    val response = repository.searchArtists(name)
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
            val songs = songDeferred.await()
            val albums = albumDeferred.await()
            val artists = artistDeferred.await()

            _sSong.value = songs
            _sAlbum.value = albums
            _sArtist.value = artists

            // Telemetry: search_query_submit
            val totalCount = (songs.size + albums.size + artists.size).toLong()
            com.example.app.analytics.AnalyticsHelper.logSearchQuerySubmit(
                keyword = name,
                hasResults = totalCount > 0,
                resultCount = totalCount
            )
        }
    }
    fun clearSuggestions() {
        searchJob?.cancel()
        _sSong.value = emptyList()
        _sAlbum.value = emptyList()
        _sArtist.value = emptyList()
    }
}