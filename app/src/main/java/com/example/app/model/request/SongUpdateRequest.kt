package com.example.app.model.request

data class SongUpdateRequest(
    val name: String,
    val description : String,
    val status : String,
    val duration : Int,
    val releasedDate : String,
    val type : String
)