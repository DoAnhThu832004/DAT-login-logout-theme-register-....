package com.example.app.model.response

data class Report(
    val id: String,
    val username: String,
    val targetType: String,
    val targetId: String,
    val issueType: String,
    val description: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)
