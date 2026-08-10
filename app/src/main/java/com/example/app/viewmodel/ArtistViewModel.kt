package com.example.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiErrorUtils
import com.example.app.model.FileUtils
import com.example.app.model.repository.ArtistRepository
import com.example.app.model.request.ArtistUpdateRequest
import com.example.app.model.response.Album
import com.example.app.model.response.Artist
import com.example.app.model.response.Playlist
import com.example.app.model.response.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class ArtistViewModel(
    private val repository: ArtistRepository
): ViewModel() {
    private val _artistState = MutableStateFlow(ArtistState())
    val artistState: StateFlow<ArtistState> = _artistState.asStateFlow()

    private val _allSongsState = MutableStateFlow<List<Song>>(emptyList())
    val allSongsState: StateFlow<List<Song>> = _allSongsState.asStateFlow()

    private val _isLoadingMoreSongs = MutableStateFlow(false)
    val isLoadingMoreSongs: StateFlow<Boolean> = _isLoadingMoreSongs.asStateFlow()

    private val _isSongsLastPage = MutableStateFlow(false)
    val isSongsLastPage: StateFlow<Boolean> = _isSongsLastPage.asStateFlow()

    private var songsCurrentPage = 1
    private var songsSearchJob: kotlinx.coroutines.Job? = null
    private var currentSongsSearchQuery: String = ""

    private val _isLoadingMoreAlbums = MutableStateFlow(false)
    val isLoadingMoreAlbums: StateFlow<Boolean> = _isLoadingMoreAlbums.asStateFlow()

    private val _isAlbumsLastPage = MutableStateFlow(false)
    val isAlbumsLastPage: StateFlow<Boolean> = _isAlbumsLastPage.asStateFlow()

    private var albumsSearchJob: kotlinx.coroutines.Job? = null
    private var currentAlbumsSearchQuery: String = ""
    private var albumsCurrentPage = 1

    private val _allAlbumsState = MutableStateFlow<List<Album>>(emptyList())
    val allAlbumsState: StateFlow<List<Album>> = _allAlbumsState.asStateFlow()

    private val _currentArtist = MutableStateFlow<Artist?>(null)
    val currentArtist: StateFlow<Artist?> = _currentArtist.asStateFlow()
    fun initCurrentArtist(artist: Artist) {
        _currentArtist.value = artist
    }
    fun getArtistById(id: String) {
        viewModelScope.launch {
            _artistState.value = _artistState.value.copy(isLoadingA = true, errorA = null)
            try {
                val response = repository.getArtistById(id)
                val body = response.body()
                if (response.isSuccessful && body?.result != null) {
                    _currentArtist.value = body.result
                    _artistState.value = _artistState.value.copy(isLoadingA = false, errorA = null)
                } else {
                    _artistState.value = _artistState.value.copy(
                        isLoadingA = false,
                        errorA = "Không tìm thấy chi tiết Artist"
                    )
                }
            } catch (e: Exception) {
                _artistState.value = _artistState.value.copy(
                    isLoadingA = false,
                    errorA = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }
    fun getArtists() {
        viewModelScope.launch {
            _artistState.value = _artistState.value.copy(isLoadingA = true, errorA = null)
            try {
                val response = repository.getArtists()
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _artistState.value = _artistState.value.copy(
                        artists = body.result,
                        isLoadingA = false,
                        errorA = null
                    )
                } else {
                    _artistState.value = _artistState.value.copy(
                        isLoadingA = false,
                        errorA = "Error fetching artists: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _artistState.value = _artistState.value.copy(
                    isLoadingA = false,
                    errorA = e.localizedMessage ?: "Unknown error"
                )
            }
        }
    }

    private var searchJob: kotlinx.coroutines.Job? = null
    private var currentSearchQuery: String = ""
    private var currentPage: Int = 1

    fun searchAdminArtists(query: String, isLoadMore: Boolean = false) {
        if (!isLoadMore) {
            searchJob?.cancel()
            currentSearchQuery = query
            currentPage = 1
        } else {
            if (_artistState.value.isLastPage || _artistState.value.isLoadingMore) return
            currentPage++
        }

        searchJob = viewModelScope.launch {
            if (!isLoadMore) kotlinx.coroutines.delay(500)

            _artistState.value = _artistState.value.copy(
                isLoadingA = !isLoadMore,
                isLoadingMore = isLoadMore,
                errorA = null
            )

            try {
                if (query.isBlank()) {
                    val response = repository.getArtists()
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.code == 1000 && body.result != null) {
                            _artistState.value = _artistState.value.copy(
                                isLoadingA = false,
                                isLoadingMore = false,
                                artists = body.result,
                                isLastPage = true,
                                errorA = null
                            )
                        } else {
                            _artistState.value = _artistState.value.copy(
                                isLoadingA = false,
                                isLoadingMore = false,
                                errorA = "Không tải được dữ liệu"
                            )
                        }
                    } else {
                        val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                        _artistState.value = _artistState.value.copy(
                            isLoadingA = false,
                            isLoadingMore = false,
                            errorA = apiErr?.message ?: "Lỗi từ hệ thống máy chủ"
                        )
                    }
                } else {
                    val response = repository.searchArtistsForAdmin(query, page = currentPage, size = 20)
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.code == 1000 && body.result != null) {
                            val pageData = body.result
                            val newArtists = pageData.result

                            val currentList = if (isLoadMore) _artistState.value.artists.orEmpty() else emptyList()
                            val updatedList = currentList + newArtists

                            _artistState.value = _artistState.value.copy(
                                isLoadingA = false,
                                isLoadingMore = false,
                                artists = updatedList,
                                isLastPage = currentPage >= pageData.totalPages,
                                errorA = null
                            )
                        } else {
                            _artistState.value = _artistState.value.copy(
                                isLoadingA = false,
                                isLoadingMore = false,
                                errorA = "Không tải được dữ liệu"
                            )
                        }
                    } else {
                        val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                        _artistState.value = _artistState.value.copy(
                            isLoadingA = false,
                            isLoadingMore = false,
                            errorA = apiErr?.message ?: "Lỗi từ hệ thống máy chủ"
                        )
                    }
                }
            } catch (e: Exception) {
                _artistState.value = _artistState.value.copy(
                    isLoadingA = false,
                    isLoadingMore = false,
                    errorA = "Lỗi đường truyền kết nối mạng: ${e.message}"
                )
                if (isLoadMore) currentPage--
            }
        }
    }
    fun createArtist(name: String, description: String) {
        viewModelScope.launch {
            _artistState.value = _artistState.value.copy(
                isLoadingA = true,
                errorA = null,
                isCreating = true
            )
            try {
                val request = com.example.app.model.request.ArtistCreationRequest(
                    name = name,
                    description = description
                )
                val response = repository.createArtist(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        val currentArtists = _artistState.value.artists?.toMutableList() ?: mutableListOf()
                        currentArtists.add(body.result)
                        _artistState.value = _artistState.value.copy(
                            isLoadingA = false,
                            isCreating = false,
                            artists = currentArtists,
                            errorA = null
                        )
                    } else {
                        _artistState.value = _artistState.value.copy(
                            isLoadingA = false,
                            isCreating = false,
                            errorA = "Failed to create artist"
                        )
                    }
                }
            } catch (e: Exception) {
                _artistState.value = _artistState.value.copy(
                    isLoadingA = false,
                    errorA = "Error: ${e.message}",
                    isCreating = false
                )
            }
        }
    }
    fun deleteArtist(id : String) {
        viewModelScope.launch {
            _artistState.value = _artistState.value.copy(
                isLoadingA = true,
                errorA = null
            )
            try {
                val response = repository.deleteArtist(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000) {
                        val currentArtists = _artistState.value.artists ?: emptyList()
                        val updatedArtists = currentArtists.filter { it.id != id }
                        _artistState.value = _artistState.value.copy(
                            isLoadingA = false,
                            artists = updatedArtists,
                            errorA = null
                        )
                    } else {
                        _artistState.value = _artistState.value.copy(
                            isLoadingA = false,
                            errorA = "Failed to delete artist"
                        )
                    }
                }
            } catch (e: Exception) {
                _artistState.value = _artistState.value.copy(
                    isLoadingA = false,
                    errorA = "Error: ${e.message}"
                )
            }
        }
    }
    fun updateArtist(id: String, name: String, description: String) {
        viewModelScope.launch {
            _artistState.value = _artistState.value.copy(
                isLoadingA = true,
                errorA = null
            )
            try {
                val request = ArtistUpdateRequest(
                    name = name,
                    description = description
                )
                val response = repository.updateArtist(id, request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        val updateArtistFromApi = body.result
                        val currentList = _artistState.value.artists ?: emptyList()
                        val updatedList = currentList.map {
                            if (it.id == id) {
                                updateArtistFromApi
                            } else {
                                it
                            }
                        }
                        _artistState.value = _artistState.value.copy(
                            isLoadingA = false,
                            artists = updatedList,
                            errorA = null
                        )
                    } else {
                        _artistState.value = _artistState.value.copy(
                            isLoadingA = false,
                            errorA = "Failed to update artist"
                        )
                    }
                }
            } catch (e: Exception) {

            }
        }

    }
    fun getAllSongs(query: String = "", isLoadMore: Boolean = false) {
        if (isLoadMore && _isSongsLastPage.value) return
        if (_isLoadingMoreSongs.value) return

        if (!isLoadMore) {
            songsSearchJob?.cancel()
            currentSongsSearchQuery = query
            songsCurrentPage = 1
            _isSongsLastPage.value = false
            _allSongsState.value = emptyList()
        } else {
            songsCurrentPage++
        }

        _isLoadingMoreSongs.value = true
        songsSearchJob = viewModelScope.launch {
            if (!isLoadMore && query.isNotBlank()) {
                kotlinx.coroutines.delay(500)
            }
            try {
                val response = if (currentSongsSearchQuery.isBlank()) {
                    repository.getSongs(page = songsCurrentPage, size = 10)
                } else {
                    repository.searchSongsForAdmin(currentSongsSearchQuery, page = songsCurrentPage, size = 10)
                }
                if (response.isSuccessful && response.body()?.result != null) {
                    val pageData = response.body()!!.result
                    val newSongs = pageData.result

                    _allSongsState.value = if (isLoadMore) {
                        _allSongsState.value + newSongs
                    } else {
                        newSongs
                    }

                    _isSongsLastPage.value = songsCurrentPage >= pageData.totalPages
                } else {
                    if (isLoadMore) songsCurrentPage--
                }
            } catch (e: Exception) {
                if (isLoadMore) songsCurrentPage--
            } finally {
                _isLoadingMoreSongs.value = false
            }
        }
    }
    fun getAllAlbums(query: String = "", isLoadMore: Boolean = false) {
        if (isLoadMore && _isAlbumsLastPage.value) return
        if (_isLoadingMoreAlbums.value) return

        if (!isLoadMore) {
            albumsSearchJob?.cancel()
            currentAlbumsSearchQuery = query
            albumsCurrentPage = 1
            _isAlbumsLastPage.value = false
            _allAlbumsState.value = emptyList()
        } else {
            albumsCurrentPage++
        }

        _isLoadingMoreAlbums.value = true
        albumsSearchJob = viewModelScope.launch {
            if (!isLoadMore && query.isNotBlank()) {
                kotlinx.coroutines.delay(500)
            }
            try {
                val response = if (currentAlbumsSearchQuery.isBlank()) {
                    repository.getAlbums(page = albumsCurrentPage, size = 10)
                } else {
                    repository.searchAlbumsForAdmin(currentAlbumsSearchQuery, page = albumsCurrentPage, size = 10)
                }
                if (response.isSuccessful && response.body()?.result != null) {
                    val pageData = response.body()!!.result
                    val newAlbums = pageData.result

                    _allAlbumsState.value = if (isLoadMore) {
                        _allAlbumsState.value + newAlbums
                    } else {
                        newAlbums
                    }

                    _isAlbumsLastPage.value = albumsCurrentPage >= pageData.totalPages
                } else {
                    if (isLoadMore) albumsCurrentPage--
                }
            } catch (e: Exception) {
                if (isLoadMore) albumsCurrentPage--
            } finally {
                _isLoadingMoreAlbums.value = false
            }
        }
    }
    fun addAlbumToArtist(artistId: String, album: Album) {
        viewModelScope.launch {
            val response = repository.addAlbumToArtist(artistId,album.id)
            if (response.isSuccessful && response.body()?.code == 1000) {
                val currentArtists = _artistState.value.artists ?: emptyList()
                val updatedArtists = currentArtists.map { artist ->
                    if (artist.id == artistId) {
                        val currentAlbums = artist.albums?.toMutableList() ?: mutableListOf()
                        if (currentAlbums.none { it.id == album.id }) {
                            currentAlbums.add(album)
                        }
                        artist.copy(albums = currentAlbums)
                    } else {
                        artist
                    }
                }
                _artistState.value = _artistState.value.copy(
                    isLoadingA = false,
                    artists = updatedArtists,
                    errorA = null
                )
            }
        }
    }
    fun addSongToArtist(artistId: String, song: Song) {
        viewModelScope.launch {
            val response = repository.addSongToArtist(artistId,song.id)
            if (response.isSuccessful && response.body()?.code == 1000) {
                val currentArtists = _artistState.value.artists ?: emptyList()
                val updatedArtists = currentArtists.map { artist ->
                    if(artist.id == artistId) {
                        val currentSongs = artist.songs?.toMutableList() ?: mutableListOf()
                        if (currentSongs.none { it.id == song.id }) {
                            currentSongs.add(song)
                        }
                        artist.copy(songs = currentSongs)
                    } else {
                        artist
                    }
                }
                _artistState.value = _artistState.value.copy(
                    isLoadingA = false,
                    artists = updatedArtists,
                    errorA = null
                )
            }
        }
    }
    fun deleteSongFromArtist(artistId : String,songId: String) {
        viewModelScope.launch {
            _artistState.value = _artistState.value.copy(
                isLoadingA = true,
                errorA = null
            )
            try {
                val response = repository.deleteSongFromArtist(artistId,songId)
                if(response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000) {
                        val currentArtists = _artistState.value.artists ?: emptyList()
                        val updatedArtists = currentArtists.map {
                            if (it.id == artistId) {
                                val updateSongs = it.songs.filter { song -> song.id != songId }
                                it.copy(songs = updateSongs)
                            } else {
                                it
                            }
                        }
                        _artistState.value = _artistState.value.copy(
                            isLoadingA = false,
                            artists = updatedArtists,
                            errorA = null
                        )
                    }
                }
            }catch (e: Exception) {
                _artistState.value = _artistState.value.copy(
                    isLoadingA = false,
                    errorA = "Error: ${e.message}"
                )
            }
        }
    }
    fun deleteAlbumFromArtist(artistId: String, albumId: String) {
        viewModelScope.launch {
            _artistState.value = _artistState.value.copy(
                isLoadingA = true,
                errorA = null
            )
            try {
                val response = repository.deleteAlbumFromArtist(artistId,albumId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000) {
                        val currentArtists = _artistState.value.artists ?: emptyList()
                        val updatedArtists = currentArtists.map {
                            if (it.id == artistId) {
                                val updateAlbums = it.albums.filter { album -> album.id != albumId }
                                it.copy(albums = updateAlbums)
                            } else {
                                it
                            }
                        }
                        _artistState.value = _artistState.value.copy(
                            isLoadingA = false,
                            artists = updatedArtists,
                            errorA = null
                        )
                    }
                }
            } catch (e: Exception) {
                _artistState.value = _artistState.value.copy(
                    isLoadingA = false,
                    errorA = "Error: ${e.message}"
                )
            }
        }

    }
    fun toggleFollow(artist: Artist) {
        val newFollowerState = !artist.followed
        updateLocalArtistFollowerStatus(artist.id, newFollowerState)
        viewModelScope.launch {
            try {
                val response = if (newFollowerState) {
                    repository.followArtist(artist.id)
                } else {
                    repository.unfollowArtist(artist.id)
                }
                if (!response.isSuccessful) {
                    updateLocalArtistFollowerStatus(artist.id, !newFollowerState)
                }
            } catch (e: Exception) {
                updateLocalArtistFollowerStatus(artist.id, !newFollowerState)
            }
        }
    }
    private fun updateLocalArtistFollowerStatus(artistId: String, isFollowing: Boolean) {
        // Ưu tiên 1: Cập nhật biến currentArtist (cho màn hình chi tiết)
        val current = _currentArtist.value
        if (current != null && current.id == artistId) {
            val newCount = if (isFollowing) {
                current.totalFollowers + 1
            } else {
                maxOf(0, current.totalFollowers - 1)
            }
            _currentArtist.value = current.copy(
                followed = isFollowing,
                totalFollowers = newCount
            )
        }

        // Ưu tiên 2: Cập nhật biến list artists (cho màn hình danh sách, nếu có)
        val currentList = _artistState.value.artists
        if (currentList != null) {
            val updatedList = currentList.map { artist ->
                if (artist.id == artistId) {
                    val newCount = if (isFollowing) {
                        artist.totalFollowers + 1
                    } else {
                        maxOf(0, artist.totalFollowers - 1)
                    }
                    artist.copy(
                        followed = isFollowing,
                        totalFollowers = newCount
                    )
                } else {
                    artist
                }
            }
            _artistState.value = _artistState.value.copy(
                artists = updatedList
            )
        }
    }
    fun getFollowerOfUser() {
        viewModelScope.launch {
            try {
                val response = repository.getMyFollowers()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000) {
                        val artists = body.result
                        _artistState.value = _artistState.value.copy(
                            artists = artists
                        )
                    }
                }
            } catch (e: Exception) {
            }
        }
    }
    fun uploadFiles(artistId: String, imageUri: Uri,context: Context) {
        viewModelScope.launch {
            _artistState.value = _artistState.value.copy(
                isLoadingA = true,
                errorA = null
            )
            try {
                val imageFile = FileUtils.getFileFromUri(context,imageUri)
                if(imageFile != null) {
                    val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("image",imageFile.name,requestBody)
                    val response = repository.uploadArtistImage(artistId,part)
                    if(response.isSuccessful) {
                        _artistState.value = _artistState.value.copy(
                            isLoadingA = false,
                            errorA = null
                        )
                    } else {
                        _artistState.value = _artistState.value.copy(
                            isLoadingA = false,
                            errorA = "Upload thất bại: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _artistState.value = _artistState.value.copy(
                    isLoadingA = false
                )
            }
        }
    }

    data class ArtistState(
        val isLoadingA: Boolean = false,
        val artists: List<Artist>? = null,
        val isLoadingMore: Boolean = false,
        val isLastPage: Boolean = false,
        val errorA: String? = null,
        val isCreating: Boolean = false,
    )
}