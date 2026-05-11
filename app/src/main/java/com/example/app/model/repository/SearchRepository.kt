package com.example.app.model.repository

import com.example.app.model.ApiService

class SearchRepository(
    private val apiService: ApiService
) {
    suspend fun searchSongs(name: String) = apiService.searchSongs(name)
    suspend fun searchAlbums(name: String, page: Int = 1, size: Int = 10) = apiService.searchAlbums(name, page, size)
    suspend fun searchArtists(name: String) = apiService.searchArtists(name)
}
