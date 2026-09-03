package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiErrorUtils
import com.example.app.model.repository.SongRepository
import com.example.app.model.response.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý danh sách bài hát yêu thích.
 * Được tạo ở cấp RecipeApp để share state giữa tất cả màn hình.
 */
class FavoriteViewModel(
    private val repository: SongRepository
) : ViewModel() {

    private val _favoriteSongs = MutableStateFlow<List<Song>>(emptyList())
    val favoriteSongs: StateFlow<List<Song>> = _favoriteSongs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Tải danh sách bài hát yêu thích từ server.
     */
    fun loadFavoriteSongs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getFavoriteSongs()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        _favoriteSongs.value = body.result
                    }
                }
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cập nhật trạng thái yêu thích local ngay lập tức (Optimistic Update)
     * và gọi API. Nếu API thất bại thì rollback.
     */
    fun toggleFavorite(song: Song, playerViewModel: PlayerViewModel) {
        val newFavoriteState = !song.favorite

        // Telemetry: song_favorite_toggle
        com.example.app.analytics.AnalyticsHelper.logSongFavoriteToggle(
            songId = song.id.toString(),
            isFavorite = newFavoriteState
        )

        // Optimistic update ngay lập tức (cập nhật UI không cần chờ API)
        if (newFavoriteState) {
            // Thêm bài vào list yêu thích ngay - kể cả khi bài chưa có trong list
            addToLocalFavorites(song)
        } else {
            // Xóa bài khỏi list yêu thích ngay
            removeFromLocalFavorites(song.id)
        }
        if (playerViewModel.currentSong.value?.id == song.id) {
            playerViewModel.currentSong.value = song.copy(favorite = newFavoriteState)
        }

        viewModelScope.launch {
            try {
                val response = if (newFavoriteState) {
                    repository.addSongToFavorite(song.id)
                } else {
                    repository.deleteSongFromFavorite(song.id)
                }

                if (!response.isSuccessful) {
                    // Rollback khi API thất bại
                    if (newFavoriteState) {
                        removeFromLocalFavorites(song.id)
                    } else {
                        addToLocalFavorites(song)
                    }
                    if (playerViewModel.currentSong.value?.id == song.id) {
                        playerViewModel.currentSong.value = song.copy(favorite = !newFavoriteState)
                    }
                }
            } catch (_: Exception) {
                // Rollback khi lỗi mạng
                if (newFavoriteState) {
                    removeFromLocalFavorites(song.id)
                } else {
                    addToLocalFavorites(song)
                }
                if (playerViewModel.currentSong.value?.id == song.id) {
                    playerViewModel.currentSong.value = song.copy(favorite = !newFavoriteState)
                }
            }
        }
    }

    /**
     * Cập nhật trạng thái yêu thích trong list local (không gọi API).
     * Dùng để đồng bộ khi các màn hình khác toggle favorite.
     */
    fun syncFavoriteState(songId: String, isFavorite: Boolean) {
        updateLocalFavorite(songId, isFavorite)
    }

    private fun updateLocalFavorite(songId: String, isFavorite: Boolean) {
        val currentList = _favoriteSongs.value
        if (isFavorite) {
            // Nếu bài hát chưa có trong list → không thêm được vì không có đủ info
            // Chỉ cập nhật nếu bài đã tồn tại trong list
            val updatedList = currentList.map { s ->
                if (s.id == songId) s.copy(favorite = true) else s
            }
            _favoriteSongs.value = updatedList
        } else {
            // Xóa bài hát khỏi list yêu thích
            _favoriteSongs.value = currentList.filter { it.id != songId }
        }
    }

    /**
     * Thêm bài hát vào danh sách yêu thích local khi user toggle từ màn hình khác.
     */
    fun addToLocalFavorites(song: Song) {
        val currentList = _favoriteSongs.value
        if (currentList.none { it.id == song.id }) {
            _favoriteSongs.value = currentList + song.copy(favorite = true)
        } else {
            _favoriteSongs.value = currentList.map { s ->
                if (s.id == song.id) s.copy(favorite = true) else s
            }
        }
    }

    /**
     * Xóa bài hát khỏi danh sách yêu thích local.
     */
    fun removeFromLocalFavorites(songId: String) {
        _favoriteSongs.value = _favoriteSongs.value.filter { it.id != songId }
    }
}
