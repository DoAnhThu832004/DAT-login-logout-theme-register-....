package com.example.app.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Artist(
    val id: String,
    val name: String,
    val imageUrlAr: String?,
    val songs: List<Song>,
    val albums: List<Album>
): Parcelable