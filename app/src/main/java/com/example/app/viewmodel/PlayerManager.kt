package com.example.app.viewmodel

import android.content.Context
import android.media.AudioManager
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.app.model.response.Song

object PlayerManager {
    private var player: ExoPlayer? = null
    private var context: Context? = null
    var currentSong: Song?  = null
        private set

    private var songList: List<Song> = emptyList()
    private var currentIndex: Int = -1
    private var shuffledList: List<Song> = emptyList()
    private var originalIndices: List<Int> = emptyList()

    var repeatMode: Int = 0
        private set
    var isShuffleMode: Boolean = false
        private set
    private var audioManager: AudioManager? = null

    var onSongChanged: ((Song) -> Unit)? = null

    fun init(context: Context) {
        this.context = context.applicationContext
        if(player == null) {
            player = ExoPlayer.Builder(context).build()
            setupPlayerListener()
        }
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }
    private fun setupPlayerListener() {
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> {
                        // Bài hát đã kết thúc, xử lý tự động chuyển bài
                        handleSongEnded()
                    }
                }
            }
        })
    }
    private fun handleSongEnded() {
        when (repeatMode) {
            2 -> {
                // Repeat one: phát lại bài hiện tại
                currentSong?.let { play(it) }
            }
            1 -> {
                // Repeat all: chuyển sang bài tiếp theo (hoặc quay lại đầu)
                next()
            }
            else -> {
                // Repeat off: chuyển sang bài tiếp theo nếu có
                next()
            }
        }
    }
    fun play(song: Song, playlist: List<Song>? = null) {
        if (player == null && context != null) {
            init(context!!)
        }
        if (playlist != null && playlist.isNotEmpty()) {
            val index = playlist.indexOfFirst { it.id == song.id }
            if (index >= 0) {
                setPlaylist(playlist, index)
            }
        }
        currentSong = song
        val mediaItem = MediaItem.fromUri(song.audioUrl.toString())
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
        onSongChanged?.invoke(song) // Trigger callback để update notification
    }
    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }
    fun getCurrentPosition(): Long {
        val position = player?.currentPosition ?: 0L
        return if (position == C.TIME_UNSET || position == C.TIME_END_OF_SOURCE || position < 0) {
            0L
        } else {
            position
        }
    }
    fun getDuration(): Long {
        val duration = player?.duration ?: 0L
        return if (duration == C.TIME_UNSET || duration == C.TIME_END_OF_SOURCE || duration < 0) {
            0L
        } else {
            duration
        }
    }
    fun seekTo(position: Long) {
        player?.seekTo(position)
    }
    fun getMaxVolume(): Int {
        return audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 100
    }

    fun getCurrentVolume(): Int {
        return audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0
    }

    fun setVolume(volume: Int) {
        audioManager?.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            volume,
            AudioManager.FLAG_SHOW_UI
        )
    }
    fun next(): Boolean {
        if (songList.isEmpty()) return false

        val nextIndex = when {
            isShuffleMode -> {
                val currentShuffledIndex = shuffledList.indexOfFirst { it.id == currentSong?.id }
                if (currentShuffledIndex >= 0 && currentShuffledIndex < shuffledList.size - 1) {
                    currentShuffledIndex + 1
                } else if (repeatMode == 1) {
                    0 // Repeat all
                } else {
                    if (currentShuffledIndex >= 0 && currentShuffledIndex < shuffledList.size - 1) {
                        currentShuffledIndex + 1
                    } else {
                        -1
                    }
                }
            }
            else -> {
                if (currentIndex < songList.size - 1) {
                    currentIndex + 1
                } else if (repeatMode == 1) {
                    0 // Repeat all
                } else {
                    if (currentIndex < songList.size - 1) {
                        currentIndex + 1
                    } else {
                        -1
                    }
                }
            }
        }

        if (nextIndex < 0) return false

        val nextSong = if (isShuffleMode) {
            if (nextIndex < shuffledList.size) shuffledList[nextIndex] else null
        } else {
            if (nextIndex < songList.size) songList[nextIndex] else null
        }

        nextSong?.let {
            currentIndex = if (isShuffleMode) {
                songList.indexOfFirst { it.id == it.id }
            } else {
                nextIndex
            }
            play(it)
            return true
        }
        return false
    }
    fun previous(): Boolean {
        if (songList.isEmpty()) return false

        val prevIndex = when {
            isShuffleMode -> {
                val currentShuffledIndex = shuffledList.indexOfFirst { it.id == currentSong?.id }
                if (currentShuffledIndex > 0) {
                    currentShuffledIndex - 1
                } else if (repeatMode == 1) {
                    shuffledList.size - 1 // Repeat all
                } else {
                    -1
                }
            }
            else -> {
                if (currentIndex > 0) {
                    currentIndex - 1
                } else if (repeatMode == 1) {
                    songList.size - 1 // Repeat all
                } else {
                    -1
                }
            }
        }

        if (prevIndex < 0) return false

        val prevSong = if (isShuffleMode) {
            if (prevIndex < shuffledList.size) shuffledList[prevIndex] else null
        } else {
            if (prevIndex < songList.size) songList[prevIndex] else null
        }

        prevSong?.let {
            currentIndex = if (isShuffleMode) {
                songList.indexOfFirst { it.id == it.id }
            } else {
                prevIndex
            }
            play(it)
            return true
        }
        return false
    }
    fun toggleRepeat(): Int {
        repeatMode = (repeatMode + 1) % 3 // 0 -> 1 -> 2 -> 0
        return repeatMode
    }
    fun toggleShuffle(): Boolean {
        isShuffleMode = !isShuffleMode
        if (isShuffleMode) {
            createShuffledList()
        }
        return isShuffleMode
    }
    fun isPlaying() : Boolean = player?.isPlaying ?: false

    fun getPlayer() : ExoPlayer? = player

    fun release() {
        player?.release()
        player = null
        currentSong = null
        songList = emptyList()
        currentIndex = -1
        shuffledList = emptyList()
        originalIndices = emptyList()
        onSongChanged = null
    }

    fun setPlaylist(songs: List<Song>, startIndex: Int = 0) {
        songList = songs
        currentIndex = startIndex.coerceIn(0,songs.size-1)
        if (isShuffleMode) {
            createShuffledList()
        }
    }

    private fun createShuffledList() {
        val indices = songList.indices.toMutableList()
        indices.shuffle()
        shuffledList = indices.map { songList[it] }
        originalIndices = indices
    }
}