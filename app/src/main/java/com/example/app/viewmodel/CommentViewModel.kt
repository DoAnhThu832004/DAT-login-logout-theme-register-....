package com.example.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.repository.CommentRepository
import com.example.app.model.request.CommentCreationRequest
import com.example.app.model.request.CommentUpdateRequest
import com.example.app.model.response.Comment
import com.example.app.model.response.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommentViewModel(
    private val repository: CommentRepository
): ViewModel() {
    private val _commentUiState = MutableStateFlow(CommentState())
    val commentState: StateFlow<CommentState> = _commentUiState.asStateFlow()

    // Biến trạng thái lưu trữ đối tượng bình luận đang được chọn để phản hồi
    var replyingToComment by mutableStateOf<Comment?>(null)
        private set

    // Phương thức thiết lập hoặc hủy bỏ trạng thái phản hồi
    fun setReplyingTo(comment: Comment?) {
        replyingToComment = comment
    }
    fun getComment(songId: String) {
        viewModelScope.launch {
            _commentUiState.value = _commentUiState.value.copy(isLoading = true, error = null)
            try {
                val response = repository.getComments(songId)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    _commentUiState.value = _commentUiState.value.copy(
                        isLoading = false,
                        comments = body.result.result,
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
                // Khởi tạo đối tượng yêu cầu với tham số parentId được trích xuất từ trạng thái hiện tại
                val request = CommentCreationRequest(
                    text = text,
                    parentId = replyingToComment?.id
                )

                val response = repository.createComment(songId, request)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        // Telemetry: song_comment_submit
                        com.example.app.analytics.AnalyticsHelper.logSongCommentSubmit(
                            songId = songId,
                            commentLength = text.length.toLong()
                        )

                        // Gọi lại phương thức lấy dữ liệu để đồng bộ hóa cấu trúc cây phân cấp từ máy chủ
                        getComment(songId)

                        // Đặt lại trạng thái phản hồi về mặc định sau khi thao tác thành công
                        setReplyingTo(null)
                    } else {
                        _commentUiState.value = _commentUiState.value.copy(isLoading = false)
                    }
                } else {
                    _commentUiState.value = _commentUiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _commentUiState.value = _commentUiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
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
                val response = repository.deleteComment(commentId)
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
                val response =  repository.updateComment(commentId,request)
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