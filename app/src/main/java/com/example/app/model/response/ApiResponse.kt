package com.example.app.model.response

data class ApiResponse<T>(
    val code: Int,
    val result: T
)
