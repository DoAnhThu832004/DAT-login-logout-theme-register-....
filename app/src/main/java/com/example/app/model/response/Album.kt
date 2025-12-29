package com.example.app.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album(
    val id: String,
    val description : String,
    val name: String,
    val status : String,
    val imageUrlA : String?,
    val songs : List<Song>?
) : Parcelable
