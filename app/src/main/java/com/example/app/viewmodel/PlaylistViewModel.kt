package com.example.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.app.model.repository.PlaylistRepository
import com.example.app.model.response.Playlist
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.example.app.model.FileUtils
import com.example.app.model.request.PlaylistCreateRequest
import com.example.app.model.request.PlaylistUpdateRequest
import com.example.app.model.response.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class PlaylistViewModel(
    private val repository: PlaylistRepository
):ViewModel() {
    private val _playlistState = MutableStateFlow(PlaylistState())
    val playlistState: StateFlow<PlaylistState> = _playlistState.asStateFlow()

    val songs = mutableStateListOf<Song>()

    private val _allSongsState = mutableStateOf<List<Song>>(emptyList())
    val allSongsState: State<List<Song>> = _allSongsState

    private val _isLoadingMoreSongs = mutableStateOf(false)
    val isLoadingMoreSongs: State<Boolean> = _isLoadingMoreSongs

    private val _isSongsLastPage = mutableStateOf(false)
    val isSongsLastPage: State<Boolean> = _isSongsLastPage

    private var songsCurrentPage = 1

    private var currentPage = 1
    private var totalPages = 1
    var isLastPage = false

    private val _currentPlaylistDetail = MutableStateFlow<Playlist?>(null)
    val currentPlaylistDetail: StateFlow<Playlist?> = _currentPlaylistDetail.asStateFlow()

    fun getPlaylistById(id: String) {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.getPlaylistById(id)
                val body = response.body()
                if (response.isSuccessful && body?.result != null) {
                    _currentPlaylistDetail.value = body.result
                    _playlistState.value = _playlistState.value.copy(isLoading = false, error = null)
                } else {
                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        error = "Lỗi trích xuất dữ liệu danh sách phát"
                    )
                }
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }
    fun getMyPlaylists() {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response =  repository.getMyPlaylists()
                val body = response.body()
                if(response.isSuccessful && body != null) {
                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        playlists = body.result,
                        error = null
                    )
                } else {
                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        error = "Failed to load playlists"
                    )
                }
            }catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    fun getPlaylists() {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response = repository.getPlaylists()
                val body = response.body()
                if(response.isSuccessful && body != null) {
                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        playlists = body.result,
                        error = null
                    )
                } else {
                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        error = "Failed to load playlists"
                    )
                }
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    fun createPlaylist(name : String, description: String) {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(
                isLoading = true,
                error = null,
                isCreating = true
            )
            try {
                val request = PlaylistCreateRequest(
                    title = name,
                    description = description
                )
                val response = repository.createPlaylist(request)
                if(response.isSuccessful) {
                    val body = response.body()
                    if(body?.code == 1000 && body.result != null) {
                        val currentPlaylists = _playlistState.value.playlists?.toMutableList() ?: mutableListOf()
                        currentPlaylists.add(body.result)
                        _playlistState.value = _playlistState.value.copy(
                            isLoading = false,
                            isCreating = false,
                            playlists = currentPlaylists,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}",
                    isCreating = false
                )
            }
        }
    }
    fun deletePlaylist(id: String) {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response = repository.deletePlaylist(id)
                if(response.isSuccessful) {
                    val body = response.body()
                    if(body?.code == 1000) {
                        val currentPlaylists = _playlistState.value.playlists ?: emptyList()
                        val updatedPlaylists = currentPlaylists.filter { it.id != id }
                        _playlistState.value = _playlistState.value.copy(
                            isLoading = false,
                            playlists = updatedPlaylists,
                            error = null
                        )
                    } else {
                        _playlistState.value = _playlistState.value.copy(
                            isLoading = false,
                            error = "Failed to delete playlist"
                        )
                    }
                }
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }

    }
    fun updatePlaylist(id: String, title: String, description: String) {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val request = PlaylistUpdateRequest(
                    title = title,
                    description = description
                )
                val response = repository.updatePlaylist(id,request)
                if(response.isSuccessful) {
                    val body = response.body()
                    if(body?.code == 1000 && body.result != null) {
                        val updatePlaylistFromApi = body.result
                        val currentList = _playlistState.value.playlists ?: emptyList()
                        val updatedList = currentList.map {
                            if (it.id == id) {
                                updatePlaylistFromApi
                            } else {
                                it
                            }
                        }
                        _playlistState.value = _playlistState.value.copy(
                            isLoading = false,
                            playlists = updatedList,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    fun uploadImage(playlistId: String, imageUri: Uri, context: Context) {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(isLoading = true, error = null)
            try {
                val imageFile = FileUtils.getFileFromUri(context,imageUri)
                if(imageFile != null) {
                    val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("image",imageFile.name,requestBody)
                    val response = repository.uploadPlaylistImage(playlistId,part)
                    if (response.isSuccessful && response.body()?.code == 1000) {
                        val updatedPlaylist = response.body()?.result
                        updatedPlaylist?.let { newPlaylist ->
                            // Cập nhật danh sách hiện tại với item mới từ Server
                            val updatedList = _playlistState.value.playlists?.map {
                                if (it.id == playlistId) newPlaylist else it
                            }
                            _playlistState.value = _playlistState.value.copy(
                                isLoading = false,
                                playlists = updatedList,
                                error = null
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _playlistState.value = _playlistState.value.copy(isLoading = false, error = "Error: ${e.message}")
            }
        }
    }
    fun getSongsInPlaylist(playlistId: String, isFirstLoad: Boolean = false) {
        if (_playlistState.value.isLoading || (isLastPage && !isFirstLoad)) return
        if (isFirstLoad) {
            currentPage = 1
            songs.clear()
            isLastPage = false
        }
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response = repository.getSongsInPlaylist(playlistId,currentPage,10)
                val body = response.body()
                if (response.isSuccessful && body != null && body.result != null) {
                    val pageData = body.result // Đây là đối tượng PageResponse

                    // Cộng dồn dữ liệu mới vào danh sách hiện tại
                    songs.addAll(pageData.result)

                    // Cập nhật thông tin phân trang
                    totalPages = pageData.totalPages
                    if (currentPage >= totalPages) {
                        isLastPage = true
                    } else {
                        currentPage++
                    }

                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    fun addSongInPlaylist(playlistId: String, song: Song) {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(isLoading = true, error = null)
            try {
                // Gọi API thêm bài hát
                val response = repository.addSongToPlaylist(playlistId, song.id)

                if (response.isSuccessful && response.body()?.code == 1000) {
                    // Logic giống AlbumViewModel: Cập nhật UI local ngay lập tức
                    if (songs.none { it.id == song.id }) {
                        songs.add(song)
                    }

                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        error = null
                    )
                } else {
                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        error = "Không thể thêm bài hát vào playlist"
                    )
                }
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = "Lỗi: ${e.message}"
                )
            }
        }
    }

    fun deleteSongInPlaylist(playlistId: String, songId: String) {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(isLoading = true, error = null)
            try {
                // Gọi API xóa bài hát
                val response = repository.deleteSongFromPlaylist(playlistId, songId)

                if (response.isSuccessful) {
                    // Logic giống AlbumViewModel: Sử dụng filter/remove để cập nhật UI ngay
                    songs.removeAll { it.id == songId }

                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        error = null
                    )
                } else {
                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        error = "Xóa bài hát thất bại"
                    )
                }
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    error = "Lỗi kết nối: ${e.message}"
                )
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
    data class PlaylistState(
        val playlists: List<Playlist>? = null,
        val isCreating: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}