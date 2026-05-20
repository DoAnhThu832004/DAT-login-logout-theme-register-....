package com.example.app.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_songs")
data class DownloadedSongEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val artistName: String,
    val duration: Int,
    val localAudioPath: String,
    val localImagePath: String
)
