package com.example.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.app.model.response.Song
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiErrorUtils
import com.example.app.model.ApiService
import com.example.app.model.FileUtils
import com.example.app.model.repository.SongRepository
import com.example.app.model.request.SongCreationRequest
import com.example.app.model.request.SongUpdateRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class SongViewModel(
    private val repository: SongRepository,
): ViewModel() {
    private val _songUiState = MutableStateFlow(SongState())
    val songState: StateFlow<SongState> = _songUiState.asStateFlow()

    private var searchJob: Job? = null
    private var currentSearchQuery: String = ""
    private var currentPage: Int = 1

    fun getTopSongs() {
        viewModelScope.launch {
            _songUiState.value = _songUiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response = repository.getTopSongs()
                if(response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            topSongs = body.result,
                            error = null
                        )
                    } else {
                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            error = "Failed to load songs"
                        )
                    }
                }
            } catch (e: Exception) {
                _songUiState.value = _songUiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    fun getSongs() {
        viewModelScope.launch {
            _songUiState.value = _songUiState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.getSongs()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            songs = body.result,
                            error = null
                        )
                    } else {
                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            error = "Failed to load songs"
                        )
                    }
                } else {
                    val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                    _songUiState.value = _songUiState.value.copy(
                        isLoading = false,
                        error = apiErr?.message ?: "Failed to load songs"
                    )
                }
            } catch (e: Exception) {
                _songUiState.value = _songUiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    fun createSong(name: String, description: String, duration: Int, releasedDate: String) {
        viewModelScope.launch {
            _songUiState.value = _songUiState.value.copy(
                isLoading = true,
                error = null,
                isCreating = true
            )
            try {
                val request = SongCreationRequest(
                    name = name,
                    description = description,
                    duration = duration,
                    releasedDate = releasedDate
                )
                val response = repository.createSong(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        val currentSongs = _songUiState.value.songs?.toMutableList() ?: mutableListOf()
                        currentSongs.add(body.result)
                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            isCreating = false,
                            songs = currentSongs,
                            isSuccessful = true,
                            error = "Song created successfully"
                        )
                    } else {
                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            isCreating = false,
                            error = "Failed to create song"
                        )
                    }
                } else {
                    val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                    _songUiState.value = _songUiState.value.copy(
                        isLoading = false,
                        isCreating = false,
                        error = apiErr?.message ?: "Failed to create song"
                    )
                }
            } catch (e: Exception) {
                _songUiState.value = _songUiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}",
                    isCreating = false
                )
            }
        }
    }
    fun deleteSong(id: String) {
        viewModelScope.launch {
            _songUiState.value = _songUiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response = repository.deleteSong(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000) {
                        val currentSongs = _songUiState.value.songs ?: emptyList()
                        val updatedSongs = currentSongs.filter { it.id != id }
                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            songs = updatedSongs,
                            error = null
                        )
                    } else {
                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            error = "Failed to delete song"
                        )
                    }
                }
            } catch (e: Exception) {
                _songUiState.value = _songUiState.value.copy(
                    isLoading = false,
                    error = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }
    fun uploadFiles(songId: String, imageFile: File, audioFile: File) {
        viewModelScope.launch {
            _songUiState.value = _songUiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response = repository.uploadSongFiles(songId, imageFile, audioFile)
                if(response.isSuccessful) {
                    _songUiState.value = _songUiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                } else {
                    _songUiState.value = _songUiState.value.copy(
                        isLoading = false,
                        error = "Failed to upload files"
                    )
                }
            } catch (e : Exception) {
                _songUiState.value = _songUiState.value.copy(isLoading = false)
            }
        }
    }
    fun updateSong(id: String, name: String, description: String, status: String, duration: Int, releasedDate: String, type: String) {
        viewModelScope.launch {
            _songUiState.value = _songUiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val request = SongUpdateRequest(
                    name = name,
                    description = description,
                    status = status,
                    duration = duration,
                    releasedDate = releasedDate,
                    type = type
                )
                val response = repository.updateSong(id, request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        val updateSongFromApi = body.result
                        val currentList = _songUiState.value.songs ?: emptyList()
                        val updatedList = currentList.map {
                            if (it.id == id) {
                                updateSongFromApi
                            } else {
                                it
                            }
                        }
                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            songs = updatedList,
                            error = null
                        )
                    } else {
                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            error = "Failed to update song"
                        )
                    }
                }
            } catch (e : Exception) {
                _songUiState.value = _songUiState.value.copy(isLoading = false)
            }
        }
    }
    fun toggleFavorite(song: Song, playerViewModel: PlayerViewModel) {
        // 1. Xác định trạng thái mới (đảo ngược trạng thái hiện tại)
        val newFavoriteState = !song.favorite

        // 2. Optimistic Update: Cập nhật UI ngay lập tức để user thấy phản hồi nhanh
        updateLocalSongFavoriteStatus(song.id, newFavoriteState)
        if (playerViewModel.currentSong.value?.id == song.id) {
            playerViewModel.currentSong.value = song.copy(favorite = newFavoriteState)
        }

        viewModelScope.launch {
            try {
                // 3. Gọi API tương ứng dựa trên trạng thái mới
                val response = if (newFavoriteState) {
                    repository.addSongToFavorite(song.id)
                } else {
                    repository.deleteSongFromFavorite(song.id)
                }

                // 4. Kiểm tra kết quả từ Server
                if (!response.isSuccessful) {
                    // Nếu thất bại (lỗi server, lỗi mạng), hoàn tác lại UI (Rollback)
                    updateLocalSongFavoriteStatus(song.id, !newFavoriteState)

                    val errorBody = response.errorBody()?.string()
                    val apiErr = ApiErrorUtils.parse(errorBody)
                    _songUiState.value = _songUiState.value.copy(
                        error = "Lỗi thao tác: ${apiErr?.message ?: response.code()}"
                    )
                }
                // Nếu thành công thì không cần làm gì thêm vì UI đã update ở bước 2

            } catch (e: Exception) {
                // Nếu có Exception (mất mạng...), hoàn tác lại UI
                updateLocalSongFavoriteStatus(song.id, !newFavoriteState)
                _songUiState.value = _songUiState.value.copy(
                    error = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }
    private fun updateLocalSongFavoriteStatus(songId: String, isFavorite: Boolean) {
        val currentList = _songUiState.value.songs ?: return

        // Tạo list mới với item đã được cập nhật (State trong Compose là bất biến)
        val updatedList = currentList.map { song ->
            if (song.id == songId) {
                song.copy(favorite = isFavorite)
            } else {
                song
            }
        }

        _songUiState.value = _songUiState.value.copy(
            songs = updatedList
        )
    }
    fun refreshSongs() {
        getSongs()
    }
    fun searchAdminSongs(query: String, isLoadMore: Boolean = false) {
        // Xử lý logic khi bắt đầu tìm kiếm từ khóa mới
        if (!isLoadMore) {
            searchJob?.cancel()
            currentSearchQuery = query
            currentPage = 1
        } else {
            // Ngăn chặn gọi API nếu đang tải hoặc đã hết trang
            if (_songUiState.value.isLastPage || _songUiState.value.isLoadingMore) return
            currentPage++
        }

        // Nếu ô tìm kiếm rỗng, khôi phục danh sách mặc định
        if (query.isBlank()) {
            getSongs()
            return
        }

        searchJob = viewModelScope.launch {
            // Chỉ áp dụng độ trễ (Debounce) cho lần gõ phím mới, không áp dụng khi cuộn trang
            if (!isLoadMore) delay(500)

            _songUiState.value = _songUiState.value.copy(
                isLoading = !isLoadMore,        // Quay loading lớn ở giữa màn hình
                isLoadingMore = isLoadMore,     // Quay loading nhỏ ở cuối màn hình
                error = null
            )

            try {
                // Truyền biến currentPage vào lời gọi API
                val response = repository.searchSongsForAdmin(query, page = currentPage, size = 20)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        val pageData = body.result
                        val newSongs = pageData.data

                        // Nếu là tải thêm (Load More), nối mảng mới vào mảng cũ
                        // Nếu là tìm kiếm mới, ghi đè mảng mới
                        val currentList = if (isLoadMore) _songUiState.value.songs.orEmpty() else emptyList()
                        val updatedList = currentList + newSongs

                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            songs = updatedList,
                            // Thuật toán kiểm tra kết thúc: Nếu trang hiện tại >= tổng số trang từ API trả về
                            isLastPage = currentPage >= pageData.totalPages,
                            error = null
                        )
                    } else {
                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = "Không tải được dữ liệu"
                        )
                    }
                } else {
                    val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                    _songUiState.value = _songUiState.value.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = apiErr?.message ?: "Lỗi từ hệ thống máy chủ"
                    )
                }
            } catch (e: Exception) {
                _songUiState.value = _songUiState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = "Lỗi đường truyền kết nối mạng"
                )
                // Lùi lại trang nếu tải lỗi để người dùng có thể thử cuộn lại
                if (isLoadMore) currentPage--
            }
        }
    }
    data class SongState(
        val songs: List<Song>? = null,
        val topSongs : List<Song>? = null,
        val isLoading: Boolean = false,
        val isLoadingMore: Boolean = false,
        val isLastPage: Boolean = false,
        val isCreating: Boolean = false,
        val isSuccessful: Boolean = false,
        val error: String? = null
    )
}