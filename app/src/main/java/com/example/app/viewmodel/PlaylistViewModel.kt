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

    private var songsPageCurrent = 1
    private var songsTotalPages = 1
    private var isSongsPlaylistLastPage = false
    val isLastPage: Boolean get() = isSongsPlaylistLastPage

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
                        myPlaylists = body.result,
                        playlists = body.result, // Cập nhật cả playlists để tương thích ngược
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
                        adminPlaylists = body.result,
                        playlists = body.result, // Cập nhật cả playlists để tương thích ngược
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

    private var searchJob: kotlinx.coroutines.Job? = null
    private var currentSearchQuery: String = ""
    private var currentPage: Int = 1

    fun searchAdminPlaylists(query: String, isLoadMore: Boolean = false) {
        if (!isLoadMore) {
            searchJob?.cancel()
            currentSearchQuery = query
            currentPage = 1
        } else {
            if (_playlistState.value.isLastPage || _playlistState.value.isLoadingMore) return
            currentPage++
        }

        searchJob = viewModelScope.launch {
            if (!isLoadMore) kotlinx.coroutines.delay(500)

            _playlistState.value = _playlistState.value.copy(
                isLoading = !isLoadMore,
                isLoadingMore = isLoadMore,
                error = null
            )

            try {
                if (query.isBlank()) {
                    val response = repository.getPlaylists()
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.code == 1000 && body.result != null) {
                            _playlistState.value = _playlistState.value.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                playlists = body.result,
                                adminPlaylists = body.result,
                                isLastPage = true,
                                error = null
                            )
                        } else {
                            _playlistState.value = _playlistState.value.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                error = "Không tải được dữ liệu"
                            )
                        }
                    } else {
                        val apiErr = com.example.app.model.ApiErrorUtils.parse(response.errorBody()?.string())
                        _playlistState.value = _playlistState.value.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = apiErr?.message ?: "Lỗi từ hệ thống máy chủ"
                        )
                    }
                } else {
                    val response = repository.searchPlaylistsForAdmin(query, page = currentPage, size = 20)
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.code == 1000 && body.result != null) {
                            val pageData = body.result
                            val newPlaylists = pageData.result

                            val currentList = if (isLoadMore) _playlistState.value.playlists.orEmpty() else emptyList()
                            val updatedList = currentList + newPlaylists

                            _playlistState.value = _playlistState.value.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                playlists = updatedList,
                                adminPlaylists = updatedList,
                                isLastPage = currentPage >= pageData.totalPages,
                                error = null
                            )
                        } else {
                            _playlistState.value = _playlistState.value.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                error = "Không tải được dữ liệu"
                            )
                        }
                    } else {
                        val apiErr = com.example.app.model.ApiErrorUtils.parse(response.errorBody()?.string())
                        _playlistState.value = _playlistState.value.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = apiErr?.message ?: "Lỗi từ hệ thống máy chủ"
                        )
                    }
                }
            } catch (e: Exception) {
                _playlistState.value = _playlistState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = "Lỗi đường truyền kết nối mạng: ${e.message}"
                )
                if (isLoadMore) currentPage--
            }
        }
    }

    fun getPlaylistsByGenre(genreId: String) {
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.getPlaylistsByGenre(genreId)
                val body = response.body()
                if (response.isSuccessful && body?.result != null) {
                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        adminPlaylists = body.result.result,
                        playlists = body.result.result,
                        error = null
                    )
                } else {
                    _playlistState.value = _playlistState.value.copy(
                        isLoading = false,
                        error = "Failed to load playlists by genre"
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
                        val currentMyPlaylists = _playlistState.value.myPlaylists?.toMutableList() ?: mutableListOf()
                        currentMyPlaylists.add(body.result)
                        _playlistState.value = _playlistState.value.copy(
                            isLoading = false,
                            isCreating = false,
                            myPlaylists = currentMyPlaylists,
                            playlists = currentMyPlaylists,
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
                        val currentMyPlaylists = _playlistState.value.myPlaylists ?: emptyList()
                        val updatedMyPlaylists = currentMyPlaylists.filter { it.id != id }
                        _playlistState.value = _playlistState.value.copy(
                            isLoading = false,
                            myPlaylists = updatedMyPlaylists,
                            playlists = updatedMyPlaylists,
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
                        val currentMyList = _playlistState.value.myPlaylists ?: emptyList()
                        val updatedMyList = currentMyList.map {
                            if (it.id == id) {
                                updatePlaylistFromApi
                            } else {
                                it
                            }
                        }
                        _playlistState.value = _playlistState.value.copy(
                            isLoading = false,
                            myPlaylists = updatedMyList,
                            playlists = updatedMyList,
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
                            val updatedMyList = _playlistState.value.myPlaylists?.map {
                                if (it.id == playlistId) newPlaylist else it
                            }
                            _playlistState.value = _playlistState.value.copy(
                                isLoading = false,
                                myPlaylists = updatedMyList,
                                playlists = updatedMyList,
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
        if (_playlistState.value.isLoading || (isSongsPlaylistLastPage && !isFirstLoad)) return
        if (isFirstLoad) {
            songsPageCurrent = 1
            songs.clear()
            isSongsPlaylistLastPage = false
        }
        viewModelScope.launch {
            _playlistState.value = _playlistState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response = repository.getSongsInPlaylist(playlistId, songsPageCurrent, 10)
                val body = response.body()
                if (response.isSuccessful && body != null && body.result != null) {
                    val pageData = body.result // Đây là đối tượng PageResponse

                    // Cộng dồn dữ liệu mới vào danh sách hiện tại
                    songs.addAll(pageData.result)

                    // Cập nhật thông tin phân trang
                    songsTotalPages = pageData.totalPages
                    if (songsPageCurrent >= songsTotalPages) {
                        isSongsPlaylistLastPage = true
                    } else {
                        songsPageCurrent++
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
        val playlists: List<Playlist>? = null, // Compatibility field
        val adminPlaylists: List<Playlist>? = null, // Store admin/global playlists
        val myPlaylists: List<Playlist>? = null,    // Store user private playlists
        val isCreating: Boolean = false,
        val isLoading: Boolean = false,
        val isLoadingMore: Boolean = false,
        val isLastPage: Boolean = false,
        val error: String? = null
    )
}