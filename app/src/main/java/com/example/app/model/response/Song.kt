package com.example.app.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    var id: String,
    val name: String,
    val description: String,
    val status : String,
    val duration: Int,
    val releasedDate: String,
    val type: String,
    val artistName: String?,
    val imageUrl: String?,
    val audioUrl: String?,
    val favorite: Boolean
): Parcelable
