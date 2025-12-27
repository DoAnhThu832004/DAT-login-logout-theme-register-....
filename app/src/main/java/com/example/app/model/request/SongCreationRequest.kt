package com.example.app.model.request

data class SongCreationRequest(
    val name: String,
    val description: String,
    val duration: Int,
    val releasedDate: String
)
