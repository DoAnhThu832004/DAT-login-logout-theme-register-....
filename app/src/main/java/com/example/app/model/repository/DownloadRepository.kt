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
    fun getAllDownloadedSongs(userId: String): Flow<List<DownloadedSongEntity>> = songDao.getAllDownloadedSongs(userId)

    /** Chỉ trả về bài hát người dùng chủ động tải (downloadedByUser = true) */
    fun getUserDownloadedSongs(userId: String): Flow<List<DownloadedSongEntity>> = songDao.getUserDownloadedSongs(userId)

    suspend fun getDownloadedSongById(songId: String, userId: String): DownloadedSongEntity? = songDao.getDownloadedSongById(songId, userId)

    suspend fun checkDownloaded(songId: String, userId: String): Boolean {
        // Check locally first with userId
        val localSong = songDao.getDownloadedSongById(songId, userId)
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

    suspend fun downloadSong(song: Song, userId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = apiService.downloadSong(song.id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val downloadDir = File(context.filesDir, "downloads")
                    if (!downloadDir.exists()) {
                        downloadDir.mkdirs()
                    }
                    // Thêm userId vào tên file để tránh xung đột nếu cần, hoặc quản lý theo sub-folder
                    val audioFile = File(downloadDir, "${userId}_${song.id}.mp3")

                    // Lưu audio file
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

                    // Tải luôn ảnh thumbnail về local để offline hiển thị được (tránh ảnh vỡ khi không có mạng)
                    var savedImagePath = song.imageUrl ?: ""
                    if (!song.imageUrl.isNullOrBlank()) {
                        try {
                            val imageFile = File(downloadDir, "${userId}_${song.id}.jpg")
                            val url = java.net.URL(song.imageUrl)
                            val imgConnection = url.openConnection()
                            imgConnection.connectTimeout = 5000
                            imgConnection.readTimeout = 5000
                            val imgInputStream = imgConnection.getInputStream()
                            val imgOutputStream = FileOutputStream(imageFile)
                            val imgBuffer = ByteArray(4096)
                            var imgBytesRead: Int
                            while (imgInputStream.read(imgBuffer).also { imgBytesRead = it } != -1) {
                                imgOutputStream.write(imgBuffer, 0, imgBytesRead)
                            }
                            imgOutputStream.flush()
                            imgOutputStream.close()
                            imgInputStream.close()
                            savedImagePath = imageFile.absolutePath
                        } catch (e: Exception) {
                            Log.w("DownloadRepository", "Could not download thumbnail image locally, fallback to URL", e)
                        }
                    }

                    // Save to Room with userId and local paths
                    val entity = DownloadedSongEntity(
                        id = song.id,
                        userId = userId,
                        name = song.name,
                        artistName = song.artistName ?: "Unknown",
                        duration = song.duration,
                        localAudioPath = audioFile.absolutePath,
                        localImagePath = savedImagePath
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

    suspend fun deleteDownload(songId: String, userId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val localSong = songDao.getDownloadedSongById(songId, userId)
            if (localSong != null) {
                val file = File(localSong.localAudioPath)
                if (file.exists()) {
                    file.delete()
                }
                if (localSong.localImagePath.isNotBlank()) {
                    val imgFile = File(localSong.localImagePath)
                    if (imgFile.exists()) {
                        imgFile.delete()
                    }
                }
                songDao.deleteSong(songId, userId)
            }
            return@withContext true
        } catch (e: Exception) {
            Log.e("DownloadRepository", "Error deleting downloaded song", e)
            return@withContext false
        }
    }
}
