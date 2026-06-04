package com.example.app.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: DownloadedSongEntity)

    @Query("SELECT * FROM downloaded_songs WHERE userId = :userId")
    fun getAllDownloadedSongs(userId: String): Flow<List<DownloadedSongEntity>>

    @Query("SELECT * FROM downloaded_songs WHERE userId = :userId AND downloadedByUser = 1")
    fun getUserDownloadedSongs(userId: String): Flow<List<DownloadedSongEntity>>

    @Query("SELECT * FROM downloaded_songs WHERE id = :songId AND userId = :userId")
    suspend fun getDownloadedSongById(songId: String, userId: String): DownloadedSongEntity?

    @Query("DELETE FROM downloaded_songs WHERE id = :songId AND userId = :userId")
    suspend fun deleteSong(songId: String, userId: String)
}