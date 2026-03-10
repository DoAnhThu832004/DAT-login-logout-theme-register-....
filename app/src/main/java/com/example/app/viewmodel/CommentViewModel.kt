package com.example.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiService
import com.example.app.model.request.AlbumCreationRequest
import com.example.app.model.request.CommentCreationRequest
import com.example.app.model.request.CommentUpdateRequest
import com.example.app.model.response.Comment
import com.example.app.model.response.Song
import com.example.app.viewmodel.AlbumViewModel.AlbumState
import kotlinx.coroutines.launch

class CommentViewModel(
    private val apiService: ApiService
): ViewModel() {
    private val _commentUiState = mutableStateOf(CommentState())
    val commentState: State<CommentState> = _commentUiState

    fun getComment(songId: String) {
        viewModelScope.launch {
            _commentUiState.value = _commentUiState.value.copy(isLoading = true, error = null)
            try {
                val response = apiService.getComments(songId)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _commentUiState.value = _commentUiState.value.copy(
                        isLoading = false,
                        comments = body.result,
                        error = null
                    )
                } else {
                    _commentUiState.value = _commentUiState.value.copy(
                        isLoading = false,
                        error = "Failed to load comments"
                    )
                }
            } catch (e: Exception) {
                _commentUiState.value = _commentUiState.value.copy(isLoading = false)
            }
        }
    }
    fun createComment(songId: String, text: String) {
        viewModelScope.launch {
            _commentUiState.value = _commentUiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val request = CommentCreationRequest(text = text)
                val response = apiService.createComment(songId,request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        val currentComments = _commentUiState.value.comments?.toMutableList() ?: mutableListOf()
                        currentComments.add(body.result)
                        _commentUiState.value = _commentUiState.value.copy(
                            isLoading = false,
                            comments = currentComments,
                            error = null
                        )
                    }
                }
            } catch (e : Exception) {
                _commentUiState.value = _commentUiState.value.copy(isLoading = false)
            }
        }
    }
    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            _commentUiState.value = _commentUiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response = apiService.deleteComment(commentId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000) {
                        val currentComments = _commentUiState.value.comments ?: emptyList()
                        val updatedComments = currentComments.filter { it.id != commentId }
                        _commentUiState.value = _commentUiState.value.copy(
                            isLoading = false,
                            comments = updatedComments,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _commentUiState.value = _commentUiState.value.copy(
                    isLoading = false,
                    error = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }
    fun updateComment(commentId: String, text: String) {
        viewModelScope.launch {
            _commentUiState.value = _commentUiState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val request = CommentUpdateRequest(text = text)
                val response =  apiService.updateComment(commentId,request)
                if(response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        val updateCommentFromApi = body.result
                        val currentList = _commentUiState.value.comments ?: emptyList()
                        val updatedList = currentList.map {
                            if (it.id == commentId) {
                                updateCommentFromApi
                            } else {
                                it
                            }
                        }
                        _commentUiState.value = _commentUiState.value.copy(
                            comments = updatedList,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e : Exception) {
                _commentUiState.value = _commentUiState.value.copy(isLoading = false)
            }
        }

    }
    data class CommentState(
        val comments: List<Comment>? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}