package com.example.app.model.request

data class ReportCreationRequest(
    val targetType: String,
    val targetId: String,
    val issueType: String,
    val description: String
)
