//package com.example.app.model.room
//
//@Dao
//interface SongDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertSong(song: DownloadedSongEntity)
//
//    @Query("SELECT * FROM downloaded_songs")
//    fun getAllDownloadedSongs(): Flow<List<DownloadedSongEntity>>
//
//    @Query("DELETE FROM downloaded_songs WHERE id = :songId")
//    suspend fun deleteSong(songId: String)
//}