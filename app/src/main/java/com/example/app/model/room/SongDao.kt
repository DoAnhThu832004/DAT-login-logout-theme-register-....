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

    @Query("SELECT * FROM downloaded_songs")
    fun getAllDownloadedSongs(): Flow<List<DownloadedSongEntity>>

    @Query("SELECT * FROM downloaded_songs WHERE id = :songId")
    suspend fun getDownloadedSongById(songId: String): DownloadedSongEntity?

    @Query("DELETE FROM downloaded_songs WHERE id = :songId")
    suspend fun deleteSong(songId: String)
}