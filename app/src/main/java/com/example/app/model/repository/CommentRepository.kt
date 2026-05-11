package com.example.app.model.repository

import com.example.app.model.ApiService
import com.example.app.model.request.CommentCreationRequest
import com.example.app.model.request.CommentUpdateRequest

class CommentRepository(
    private val apiService: ApiService
) {
    suspend fun getComments(songId: String, page: Int = 1, size: Int = 10) = apiService.getComments(songId, page, size)
    suspend fun createComment(songId: String, request: CommentCreationRequest) = apiService.createComment(songId, request)
    suspend fun deleteComment(commentId: String) = apiService.deleteComment(commentId)
    suspend fun updateComment(commentId: String, request: CommentUpdateRequest) = apiService.updateComment(commentId, request)
}