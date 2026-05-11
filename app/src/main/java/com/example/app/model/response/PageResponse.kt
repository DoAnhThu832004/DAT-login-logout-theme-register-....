package com.example.app.model.response

data class PageResponse<T>(
    val currentPage: Int,
    val totalPages: Int,
    val pageSize: Int,
    val totalElements: Long,
    val result: List<T>
)
