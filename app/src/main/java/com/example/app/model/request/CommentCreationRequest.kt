package com.example.app.model.request

data class CommentCreationRequest(
    val text : String,
    val parentId : String? = null
)