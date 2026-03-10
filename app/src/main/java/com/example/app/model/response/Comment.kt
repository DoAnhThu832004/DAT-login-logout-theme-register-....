package com.example.app.model.response

data class Comment(
    val id : String,
    val text : String,
    val edited : Boolean,
    val createAt : String,
    val username : String,
    val songTitle : String,
    val owner: Boolean
)