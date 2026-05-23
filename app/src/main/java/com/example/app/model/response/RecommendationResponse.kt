package com.example.app.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecommendationResponse(
    val source: String,       // "PERSONALIZED" | "COLD_START_GENRE" | "COLD_START_GLOBAL"
    val totalCount: Int,
    val songs: List<Song>
) : Parcelable
