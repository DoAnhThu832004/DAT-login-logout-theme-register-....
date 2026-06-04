package com.example.app.model.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.app.model.ApiService
import com.example.app.model.paging.AlbumPagingSource
import com.example.app.model.paging.SongPagingSource
import com.example.app.model.request.AlbumCreationRequest
import com.example.app.model.request.AlbumUpdateRequest
import com.example.app.model.response.Album
import com.example.app.model.response.Song
import kotlinx.coroutines.flow.Flow

class AlbumRepository(
    private val apiService: ApiService
) {
    suspend fun getAlbumById(id: String) = apiService.getAlbumById(id)
    suspend fun getAlbums() = apiService.getAlbums()
    suspend fun createAlbum(request: AlbumCreationRequest) = apiService.createAlbum(request)
    suspend fun deleteAlbum(id: String) = apiService.deleteAlbum(id)
    suspend fun updateAlbum(id: String, request: AlbumUpdateRequest) = apiService.updateAlbum(id, request)
    suspend fun deleteSongFromAlbum(albumId: String, songId: String) = apiService.deleteSongFromAlbum(albumId, songId)
    suspend fun addSongToAlbum(albumId: String, songId: String) = apiService.addSongToAlbum(albumId, songId)
    suspend fun getSongs(page: Int = 1, size: Int = 10) = apiService.getSongs(page, size)

    suspend fun uploadAlbumImage(albumId: String, imagePart: okhttp3.MultipartBody.Part) = apiService.uploadAlbumImage(albumId, imagePart)
    suspend fun getAlbumsByGenre(genreId: String, page: Int = 1, size: Int = 10) = apiService.getAlbumsByGenre(genreId, page, size)

    fun getAlbumsPaging(): Flow<PagingData<Album>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { AlbumPagingSource(apiService) }
        ).flow
    }
}