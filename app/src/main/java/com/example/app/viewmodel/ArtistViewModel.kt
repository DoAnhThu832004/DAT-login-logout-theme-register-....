package com.example.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiErrorUtils
import com.example.app.model.ApiService
import com.example.app.model.request.AlbumCreationRequest
import com.example.app.model.request.AlbumUpdateRequest
import com.example.app.model.request.ArtistUpdateRequest
import com.example.app.model.response.Album
import com.example.app.model.response.Artist
import com.example.app.model.response.Song
import kotlinx.coroutines.launch

class ArtistViewModel(
    private val apiService: ApiService
): ViewModel() {
    private val _artistState = mutableStateOf(ArtistState())
    val artistState: State<ArtistState> = _artistState

    private val _allSongsState = mutableStateOf<List<Song>>(emptyList())
    val allSongsState: State<List<Song>> = _allSongsState

    private val _allAlbumsState = mutableStateOf<List<Album>>(emptyList())
    val allAlbumsState: State<List<Album>> = _allAlbumsState

    fun getArtists() {
        viewModelScope.launch {
            _artistState.value = _artistState.value.copy(isLoadingA = true, errorA = null)
            try {
                val response = apiService.getArtists()
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
                val response = apiService.createArtist(request)
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
                val response = apiService.deleteArtist(id)
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
                val response = apiService.updateArtist(id, request)
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
    fun getAllSongs() {
        viewModelScope.launch {
            val response = apiService.getSongs()
            if (response.isSuccessful && response.body()?.result != null) {
                _allSongsState.value = response.body()!!.result
            }
        }
    }
    fun getAllAlbums() {
        viewModelScope.launch {
            val response = apiService.getAlbums()
            if (response.isSuccessful && response.body()?.result != null) {
                _allAlbumsState.value = response.body()!!.result
            }
        }
    }
    fun addAlbumToArtist(artistId: String, album: Album) {
        viewModelScope.launch {
            val response = apiService.addAlbumToArtist(artistId,album.id)
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
            val response = apiService.addSongToArtist(artistId,song.id)
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
                val response = apiService.deleteSongFromArtist(artistId,songId)
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
                val response = apiService.deleteAlbumFromArtist(artistId,albumId)
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
                    apiService.followArtist(artist.id)
                } else {
                    apiService.unfollowArtist(artist.id)
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
        val currentList = _artistState.value.artists ?: return
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

    data class ArtistState(
        val isLoadingA: Boolean = false,
        val artists: List<Artist>? = null,
        val errorA: String? = null,
        val isCreating: Boolean = false,
    )
}