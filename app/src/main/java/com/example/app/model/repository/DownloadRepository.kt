package com.example.app.model.repository

import android.content.Context
import android.util.Log
import com.example.app.model.ApiService
import com.example.app.model.response.Song
import com.example.app.model.room.DownloadedSongEntity
import com.example.app.model.room.SongDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class DownloadRepository(
    private val apiService: ApiService,
    private val songDao: SongDao,
    private val context: Context
) {
    fun getAllDownloadedSongs(): Flow<List<DownloadedSongEntity>> = songDao.getAllDownloadedSongs()

    /** Chỉ trả về bài hát người dùng chủ động tải (downloadedByUser = true) */
    fun getUserDownloadedSongs(): Flow<List<DownloadedSongEntity>> = songDao.getUserDownloadedSongs()

    suspend fun getDownloadedSongById(songId: String): DownloadedSongEntity? = songDao.getDownloadedSongById(songId)

    suspend fun checkDownloaded(songId: String): Boolean {
        // Check locally first
        val localSong = songDao.getDownloadedSongById(songId)
        if (localSong != null) {
            val file = File(localSong.localAudioPath)
            if (file.exists()) return true
        }

        // Check remote API
        return try {
            val response = apiService.checkDownloaded(songId)
            response.body()?.result ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun downloadSong(song: Song): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = apiService.downloadSong(song.id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val downloadDir = File(context.filesDir, "downloads")
                    if (!downloadDir.exists()) {
                        downloadDir.mkdirs()
                    }
                    val audioFile = File(downloadDir, "${song.id}.mp3")

                    val inputStream = body.byteStream()
                    val outputStream = FileOutputStream(audioFile)

                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()

                    // Save to Room
                    val entity = DownloadedSongEntity(
                        id = song.id,
                        name = song.name,
                        artistName = song.artistName ?: "Unknown",
                        duration = song.duration,
                        localAudioPath = audioFile.absolutePath,
                        localImagePath = song.imageUrl ?: ""
                    )
                    songDao.insertSong(entity)
                    return@withContext true
                }
            }
            return@withContext false
        } catch (e: Exception) {
            Log.e("DownloadRepository", "Error downloading song", e)
            return@withContext false
        }
    }

    suspend fun deleteDownload(songId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val localSong = songDao.getDownloadedSongById(songId)
            if (localSong != null) {
                val file = File(localSong.localAudioPath)
                if (file.exists()) {
                    file.delete()
                }
                songDao.deleteSong(songId)
            }
            return@withContext true
        } catch (e: Exception) {
            Log.e("DownloadRepository", "Error deleting downloaded song", e)
            return@withContext false
        }
    }
}
