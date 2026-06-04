package com.example.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.app.model.ApiErrorUtils
import com.example.app.model.FileUtils
import com.example.app.model.repository.AlbumRepository
import com.example.app.model.request.AlbumCreationRequest
import com.example.app.model.request.AlbumUpdateRequest
import com.example.app.model.response.Album
import com.example.app.model.response.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.internal.toImmutableList

class AlbumViewModel(
    private val repository: AlbumRepository
) : ViewModel() {
    private val _albumUiState = MutableStateFlow(AlbumState())
    val albumState: StateFlow<AlbumState> = _albumUiState.asStateFlow()

    private val _allSongsState = MutableStateFlow<List<Song>>(emptyList())
    val allSongsState: StateFlow<List<Song>> = _allSongsState.asStateFlow()

    private val _isLoadingMoreSongs = MutableStateFlow(false)
    val isLoadingMoreSongs: StateFlow<Boolean> = _isLoadingMoreSongs.asStateFlow()

    private val _isSongsLastPage = MutableStateFlow(false)
    val isSongsLastPage: StateFlow<Boolean> = _isSongsLastPage.asStateFlow()

    private var songsCurrentPage = 1

    private val _currentAlbumDetail = MutableStateFlow<Album?>(null)
    val currentAlbumDetail: StateFlow<Album?> = _currentAlbumDetail.asStateFlow()

//    val albumsPaging: Flow<PagingData<Album>> =
//        repository
//            .getAlbumsPaging()
//            .cachedIn(viewModelScope)

    fun getAlbumById(id: String) {
        viewModelScope.launch {
            // Tận dụng biến isLoading có sẵn trong AlbumState để bật hiệu ứng tải
            _albumUiState.value = _albumUiState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.getAlbumById(id)
                val body = response.body()
                if (response.isSuccessful && body?.result != null) {
                    // Cập nhật dữ liệu vào biến currentAlbumDetail
                    _currentAlbumDetail.value = body.result
                    _albumUiState.value = _albumUiState.value.copy(isLoading = false, error = null)
                } else {
                    _albumUiState.value = _albumUiState.value.copy(
                        isLoading = false,
                        error = "Không tìm thấy chi tiết Album"
                    )
                }
            } catch (e: Exception) {
                _albumUiState.value = _albumUiState.value.copy(
                    isLoading = false,
                    error = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }

    fun getAlbums() {
        viewModelScope.launch {
            _albumUiState.value = _albumUiState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.getAlbums()
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _albumUiState.value = _albumUiState.value.copy(
                        isLoading = false,
                        albums = body.result.result,
                        error = null
                    )
                } else {
                    _albumUiState.value = _albumUiState.value.copy(
                        isLoading = false,
                        error = "Failed to load albums"
                    )
                }
            } catch (e : Exception) {
                _albumUiState.value = _albumUiState.value.copy(isLoading = false)
            }
        }
    }

    fun getAlbumsByGenre(genreId: String) {
        viewModelScope.launch {
            _albumUiState.value = _albumUiState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.getAlbumsByGenre(genreId)
                val body = response.body()
                if (response.isSuccessful && body?.result != null) {
                    _albumUiState.value = _albumUiState.value.copy(
                        isLoading = false,
                        albums = body.result.result,
                        error = null
                    )
                } else {
                    _albumUiState.value = _albumUiState.value.copy(
                        isLoading = false,
                        error = "Failed to load albums by genre"
                    )
                }
            } catch (e: Exception) {
                _albumUiState.value = _albumUiState.value.copy(isLoading = false)
            }
        }
    }
    fun createAlbum(name: String, description: String) {
        viewModelScope.launch {
            _albumUiState.value = _albumUiState.value.copy(
                isLoading = true,
                error = null,
                isCreating = true
            )
            try {
                val request = AlbumCreationRequest(
                    name = name,
                    description = description
                )
                val response = repository.createAlbum(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        val currentAlbums = _albumUiState.value.albums?.toMutableList() ?: mutableListOf()
                        currentAlbums.add(body.result)
                        _albumUiState.value = _albumUiState.value.copy(
                            isLoading = false,
                            isCreating = false,
                            albums = currentAlbums,
                            error = null
                        )
                    } else {
                        _albumUiState.value = _albumUiState.value.copy(
                            isLoading = false,
                            isCreating = false,
                            error = "Failed to create album"
                        )
                    }
                }
            } catch (e: Exception) {
                _albumUiState.value = _albumUiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}",
                    isCreating = false
                )
            }
        }
    }
    fun deleteAlbum(id: String) {
        viewModelScope.launch {
            _albumUiState.value = _albumUiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response = repository.deleteAlbum(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000) {
                        val currentAlbums = _albumUiState.value.albums ?: emptyList()
                        val updatedAlbums = currentAlbums.filter { it.id != id }
                        _albumUiState.value = _albumUiState.value.copy(
                            isLoading = false,
                            albums = updatedAlbums,
                            error = null
                        )
                    } else {
                        _albumUiState.value = _albumUiState.value.copy(
                            isLoading = false,
                            error = "Failed to delete album"
                        )
                    }
                }
            }catch (e: Exception) {
                _albumUiState.value = _albumUiState.value.copy(
                    isLoading = false,
                    error = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }
    fun updateAlbum(id: String, name: String, description: String, status: String) {
        viewModelScope.launch {
            _albumUiState.value = _albumUiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val request = AlbumUpdateRequest(
                    name = name,
                    description = description,
                    status = status
                )
                val response = repository.updateAlbum(id, request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        val updateAlbumFromApi = body.result
                        val currentList = _albumUiState.value.albums ?: emptyList()
                        val updatedList = currentList.map {
                            if (it.id == id) {
                                updateAlbumFromApi
                            } else {
                                it
                            }
                        }
                        _albumUiState.value = _albumUiState.value.copy(
                            isLoading = false,
                            albums = updatedList,
                            error = null
                        )
                    }
                } else {
                    _albumUiState.value = _albumUiState.value.copy(
                        isLoading = false,
                        error = "Failed to update album"
                    )
                }
            } catch (e : Exception) {
                _albumUiState.value = _albumUiState.value.copy(isLoading = false)
            }
        }
    }
    fun deleteSongFromAlbum(albumId: String, songId: String) {
        viewModelScope.launch {
            _albumUiState.value = _albumUiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response = repository.deleteSongFromAlbum(albumId, songId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000) {
                        val currentAlbums = _albumUiState.value.albums ?: emptyList()
                        val updatedAlbums = currentAlbums.map { album ->
                            if (album.id == albumId) {
                                // Nếu đúng là Album cần sửa:
                                // 1. Lọc bỏ bài hát có songId
                                val updatedSongs = album.songs?.filter { song -> song.id != songId }

                                // 2. Trả về bản sao của Album với danh sách bài hát mới
                                album.copy(songs = updatedSongs)
                            } else {
                                // Nếu không phải Album này, giữ nguyên
                                album
                            }
                        }
                        _albumUiState.value = _albumUiState.value.copy(
                            isLoading = false,
                            albums = updatedAlbums,
                            error = null
                        )
                    } else {
                        _albumUiState.value = _albumUiState.value.copy(
                            isLoading = false,
                            error = "Failed to delete song from album"
                        )
                    }
                }
            } catch (e : Exception) {
                _albumUiState.value = _albumUiState.value.copy(isLoading = false)
            }
        }
    }
    fun getAllSongs(isLoadMore: Boolean = false) {
        if (isLoadMore && _isSongsLastPage.value) return
        if (_isLoadingMoreSongs.value) return

        if (!isLoadMore) {
            songsCurrentPage = 1
            _isSongsLastPage.value = false
            _allSongsState.value = emptyList()
        }

        _isLoadingMoreSongs.value = true
        viewModelScope.launch {
            try {
                val response = repository.getSongs(page = songsCurrentPage, size = 10)
                if (response.isSuccessful && response.body()?.result != null) {
                    val pageData = response.body()!!.result
                    val newSongs = pageData.result

                    _allSongsState.value = if (isLoadMore) {
                        _allSongsState.value + newSongs
                    } else {
                        newSongs
                    }

                    _isSongsLastPage.value = songsCurrentPage >= pageData.totalPages
                    if (!_isSongsLastPage.value) {
                        songsCurrentPage++
                    }
                }
            } catch (e: Exception) {
                // handle error
            } finally {
                _isLoadingMoreSongs.value = false
            }
        }
    }
    fun addSongToAlbum(albumId: String, song: Song) { // Truyền cả Object Song để update UI
        viewModelScope.launch {
            try {
                val response = repository.addSongToAlbum(albumId, song.id)

                if (response.isSuccessful && response.body()?.code == 1000) {
                    val currentAlbums = _albumUiState.value.albums ?: emptyList()

                    val updatedAlbums = currentAlbums.map { album ->
                        if (album.id == albumId) {
                            val currentSongs = album.songs?.toMutableList() ?: mutableListOf()
                            if (currentSongs.none { it.id == song.id }) {
                                currentSongs.add(song)
                            }
                            album.copy(songs = currentSongs)
                        } else {
                            album
                        }
                    }

                    _albumUiState.value = _albumUiState.value.copy(
                        isLoading = false,
                        albums = updatedAlbums,
                        error = null
                    )
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }
    fun uploadFiles(albumId: String, imageUri: Uri,context: Context) {
        viewModelScope.launch {
            _albumUiState.value = _albumUiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val imageFile = FileUtils.getFileFromUri(context,imageUri)
                if(imageFile != null) {
                    val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("image",imageFile.name,requestBody)
                    val response = repository.uploadAlbumImage(albumId,part)
                    if(response.isSuccessful) {
                        _albumUiState.value = _albumUiState.value.copy(
                            isLoading = false,
                            error = null
                        )
                    } else {
                        _albumUiState.value = _albumUiState.value.copy(
                            isLoading = false,
                            error = "Upload thất bại: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _albumUiState.value = _albumUiState.value.copy(
                    isLoading = false
                )
            }
        }
    }
    data class AlbumState(
        val albums: List<Album>? = null,
        val isCreating: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}