package com.example.app.model.repository

import com.example.app.model.ApiService
import com.example.app.model.request.PlaylistCreateRequest
import com.example.app.model.request.PlaylistUpdateRequest
import okhttp3.MultipartBody

class PlaylistRepository(
    private val apiService: ApiService
) {
    suspend fun getPlaylists() = apiService.getPlaylists()
    suspend fun getPlaylistById(id: String) = apiService.getPlaylistById(id)
    suspend fun getMyPlaylists() = apiService.getMyPlaylists()
    suspend fun createPlaylist(request: PlaylistCreateRequest) = apiService.createPlaylist(request)
    suspend fun deletePlaylist(id: String) = apiService.deletePlaylist(id)
    suspend fun updatePlaylist(id: String, request: PlaylistUpdateRequest) = apiService.updatePlaylist(id, request)
    suspend fun getSongsInPlaylist(playlistId: String, page: Int = 1, size: Int = 10) = apiService.getSongsInPlaylist(playlistId, page, size)
    suspend fun addSongToPlaylist(playlistId: String, songId: String) = apiService.addSongToPlaylist(playlistId, songId)
    suspend fun deleteSongFromPlaylist(playlistId: String, songId: String) = apiService.deleteSongFromPlaylist(playlistId, songId)
    suspend fun uploadPlaylistImage(playlistId: String, imagePart: MultipartBody.Part) = apiService.uploadPlaylistImage(playlistId, imagePart)
    suspend fun getSongs(page: Int = 1, size: Int = 10) = apiService.getSongs(page, size)
    suspend fun getPlaylistsByGenre(genreId: String, page: Int = 1, size: Int = 10) = apiService.getPlaylistsByGenre(genreId, page, size)
}