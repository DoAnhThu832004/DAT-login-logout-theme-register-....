package com.example.app.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Artist(
    val id: String,
    val name: String,
    val description: String,
    val imageUrlAr: String?,
    val songs: List<Song>,
    val albums: List<Album>,
    val totalFollowers: Int,
    val followed: Boolean
): Parcelable