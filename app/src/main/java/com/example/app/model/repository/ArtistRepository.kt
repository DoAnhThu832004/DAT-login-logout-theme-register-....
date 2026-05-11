package com.example.app.model.repository

import com.example.app.model.ApiService
import com.example.app.model.request.ArtistCreationRequest
import com.example.app.model.request.ArtistUpdateRequest

class ArtistRepository(
    private val apiService: ApiService
) {
    suspend fun getArtists() = apiService.getArtists()
    suspend fun getArtistById(id: String) = apiService.getArtistById(id)
    suspend fun searchArtists(name: String) = apiService.searchArtists(name)
    suspend fun createArtist(request: ArtistCreationRequest) = apiService.createArtist(request)
    suspend fun deleteArtist(id: String) = apiService.deleteArtist(id)
    suspend fun updateArtist(id: String, request: ArtistUpdateRequest) = apiService.updateArtist(id, request)
    suspend fun addSongToArtist(artistId: String, songId: String) = apiService.addSongToArtist(artistId, songId)
    suspend fun addAlbumToArtist(artistId: String, albumId: String) = apiService.addAlbumToArtist(artistId, albumId)
    suspend fun deleteAlbumFromArtist(artistId: String, albumId: String) = apiService.deleteAlbumFromArtist(artistId, albumId)
    suspend fun deleteSongFromArtist(artistId: String, songId: String) = apiService.deleteSongFromArtist(artistId, songId)
    suspend fun followArtist(artistId: String) = apiService.followArtist(artistId)
    suspend fun unfollowArtist(artistId: String) = apiService.unfollowArtist(artistId)
    suspend fun getMyFollowers() = apiService.getMyFollowers()
    suspend fun getSongs(page: Int = 1, size: Int = 10) = apiService.getSongs(page, size)
    suspend fun getAlbums(page: Int = 1, size: Int = 10) = apiService.getAlbums(page, size)
    suspend fun uploadArtistImage(artistId: String, imagePart: okhttp3.MultipartBody.Part) = apiService.uploadArtistImage(artistId, imagePart)
}