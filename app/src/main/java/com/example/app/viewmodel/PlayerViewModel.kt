package com.example.app.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.response.Song
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {
    val currentSong = mutableStateOf<Song?>(null)
    val isPlaying = mutableStateOf(false)
    val repeatMode = mutableStateOf(0)
    val isShuffleMode = mutableStateOf(false)

    // Seek states
    val currentPosition = mutableStateOf(0L)
    val duration = mutableStateOf(0L)

    // Volume states
    val currentVolume = mutableStateOf(0)
    val maxVolume = mutableStateOf(100)

    init {
        // Lắng nghe khi bài hát thay đổi (tự động chuyển bài)
        PlayerManager.onSongChanged = { song ->
            currentSong.value = song
            isPlaying.value = true
            updateDuration()
        }
    }

    fun play(song: Song, playlist: List<Song>? = null) {
        PlayerManager.play(song, playlist)
        currentSong.value = song
        isPlaying.value = true
        repeatMode.value = PlayerManager.repeatMode
        isShuffleMode.value = PlayerManager.isShuffleMode
//        viewModelScope.launch {
//            retrofitService.listenSong(song.id)
//        }
        updateDuration()
        updateVolume()
    }

    fun next() {
        if(PlayerManager.next()) {
            currentSong.value = PlayerManager.currentSong
            isPlaying.value = true
            updateDuration()
        }
    }
    fun previous() {
        if(PlayerManager.previous()) {
            currentSong.value = PlayerManager.currentSong
            isPlaying.value = true
            updateDuration()
        }
    }
    fun toggleRepeat() {
        repeatMode.value = PlayerManager.toggleRepeat()
    }
    fun toggleShuffle() {
        isShuffleMode.value = PlayerManager.toggleShuffle()
    }
    fun togglePlayPause() {
        PlayerManager.togglePlayPause()
        isPlaying.value = PlayerManager.isPlaying()
    }
    // Seek functions
    fun seekTo(position: Long) {
        PlayerManager.seekTo(position)
        currentPosition.value = position
    }

    fun updatePosition() {
        currentPosition.value = PlayerManager.getCurrentPosition()
    }

    fun updateDuration() {
        duration.value = PlayerManager.getDuration()
    }
    // Volume functions
    fun setVolume(volume: Int) {
        PlayerManager.setVolume(volume)
        currentVolume.value = volume
    }

    fun updateVolume() {
        currentVolume.value = PlayerManager.getCurrentVolume()
        maxVolume.value = PlayerManager.getMaxVolume()
    }
    override fun onCleared() {
        PlayerManager.release()
        PlayerManager.release()
    }
}