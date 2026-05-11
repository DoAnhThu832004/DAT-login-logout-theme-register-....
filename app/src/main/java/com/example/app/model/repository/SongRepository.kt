package com.example.app.model.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.app.model.ApiService
import com.example.app.model.paging.SongPagingSource
import com.example.app.model.request.SongCreationRequest
import com.example.app.model.request.SongUpdateRequest
import com.example.app.model.response.Song
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import androidx.paging.Pager
import kotlinx.coroutines.flow.Flow

class SongRepository(
    private val apiService: ApiService
) {
    suspend fun getTopSongs() = apiService.getTopSongs()
    suspend fun getSongs() = apiService.getSongs()
    suspend fun createSong(request: SongCreationRequest) = apiService.createSong(request)
    suspend fun deleteSong(id: String) = apiService.deleteSong(id)
    suspend fun updateSong(id: String, request: SongUpdateRequest) = apiService.updateSong(id, request)
    suspend fun addSongToFavorite(songId: String) = apiService.addSongToFavorite(songId)
    suspend fun deleteSongFromFavorite(songId: String) = apiService.deleteSongFromFavorite(songId)
    suspend fun searchSongsForAdmin(query: String, page: Int, size: Int) = apiService.searchSongsForAdmin(query, page, size)
    suspend fun uploadSongFiles(songId: String, imageFile: File, audioFile: File): retrofit2.Response<com.example.app.model.response.ApiResponse<com.example.app.model.response.Song>> {
        val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)

        val audioRequestBody = audioFile.asRequestBody("audio/*".toMediaTypeOrNull())
        val audioPart = MultipartBody.Part.createFormData("audio", audioFile.name, audioRequestBody)

        return apiService.uploadSongFiles(songId, imagePart, audioPart)
    }
    suspend fun downloadSong(songId: String) = apiService.downloadSong(songId)
    fun getSongsPaging(query: String? = null): Flow<PagingData<Song>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { SongPagingSource(apiService, query) }
        ).flow
    }
}