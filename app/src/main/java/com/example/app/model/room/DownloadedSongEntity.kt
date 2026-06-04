package com.example.app.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_songs", primaryKeys = ["id", "userId"])
data class DownloadedSongEntity(
    val id: String,
    val userId: String, // ID của người dùng đã tải bài hát này
    val name: String,
    val artistName: String,
    val duration: Int,
    val localAudioPath: String,
    val localImagePath: String,
    val downloadedByUser: Boolean = true
)
