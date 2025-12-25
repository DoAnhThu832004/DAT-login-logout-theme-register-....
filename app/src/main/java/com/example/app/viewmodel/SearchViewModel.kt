package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiService
import com.example.app.model.response.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val retrofitService: ApiService
):ViewModel() {
    private val _sSong = MutableStateFlow<List<Song>>(emptyList())
    val sSong = _sSong.asStateFlow()
    private var searchJob: Job? = null

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
    // Trong SearchViewModel.kt
    private suspend fun performSearch(name: String) {
        try {
            val response = retrofitService.searchSongs(name)
            if (response.isSuccessful) {
                // SỬA LỖI TẠI ĐÂY:
                // Lấy trực tiếp result, không dùng listOf() bao bọc nó nữa.
                val songs = response.body()?.result ?: emptyList()

                // Gán trực tiếp, không cần ép kiểu "as List<Song>" thiếu an toàn
                _sSong.value = songs
            } else {
                _sSong.value = emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _sSong.value = emptyList()
        }
    }
    fun clearSuggestions() {
        searchJob?.cancel()
        _sSong.value = emptyList()
    }
}