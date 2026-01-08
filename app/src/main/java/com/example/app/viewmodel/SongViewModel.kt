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
import com.example.app.model.request.SongCreationRequest
import com.example.app.model.request.SongUpdateRequest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class SongViewModel(
    private val apiService: ApiService,
): ViewModel() {
    private val _songUiState = mutableStateOf(SongState())
    val songState: State<SongState> = _songUiState

    fun getSongs() {
        viewModelScope.launch {
            _songUiState.value = _songUiState.value.copy(isLoading = true, error = null)
            try {
                val response = apiService.getSongs()
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
                val response = apiService.createSong(request)
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
                val response = apiService.deleteSong(id)
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
    fun uploadFiles(songId: String, imageUri: Uri, audioUri: Uri, context: Context) {
        viewModelScope.launch {
            _songUiState.value = _songUiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val imageFile = FileUtils.getFileFromUri(context, imageUri)
                val audioFile = FileUtils.getFileFromUri(context, audioUri)
                if (imageFile != null && audioFile != null) {
                    // 2. Tạo RequestBody cho Image
                    val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                    // createFormData tham số 1 là "key" trong Postman ("image")
                    val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)

                    // 3. Tạo RequestBody cho Audio
                    val audioRequestBody = audioFile.asRequestBody("audio/*".toMediaTypeOrNull())
                    // createFormData tham số 1 là "key" trong Postman ("audio")
                    val audioPart = MultipartBody.Part.createFormData("audio", audioFile.name, audioRequestBody)

                    // 4. Gọi API
                    val response = apiService.uploadSongFiles(songId, imagePart, audioPart)

                    if (response.isSuccessful) {
                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            error = null
                        )
                    } else {
                        _songUiState.value = _songUiState.value.copy(
                            isLoading = false,
                            error = "Upload thất bại: ${response.code()}"
                        )
                    }
                } else {
                    _songUiState.value = _songUiState.value.copy(isLoading = false, error = "Không tìm thấy file")
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
                val response = apiService.updateSong(id, request)
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
    fun toggleFavorite(song: Song) {
        // 1. Xác định trạng thái mới (đảo ngược trạng thái hiện tại)
        val newFavoriteState = !song.favorite

        // 2. Optimistic Update: Cập nhật UI ngay lập tức để user thấy phản hồi nhanh
        updateLocalSongFavoriteStatus(song.id, newFavoriteState)

        viewModelScope.launch {
            try {
                // 3. Gọi API tương ứng dựa trên trạng thái mới
                val response = if (newFavoriteState) {
                    apiService.addSongToFavorite(song.id)
                } else {
                    apiService.deleteSongFromFavorite(song.id)
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
    data class SongState(
        val songs: List<Song>? = null,
        val isLoading: Boolean = false,
        val isCreating: Boolean = false,
        val isSuccessful: Boolean = false,
        val error: String? = null
    )
}