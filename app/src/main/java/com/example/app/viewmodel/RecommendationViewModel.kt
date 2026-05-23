package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiErrorUtils
import com.example.app.model.repository.SongRepository
import com.example.app.model.response.RecommendationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecommendationViewModel(
    private val repository: SongRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RecommendationState())
    val state: StateFlow<RecommendationState> = _state.asStateFlow()

    fun getRecommendations(userId: String, limit: Int = 10) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val response = repository.getRecommendations(userId, limit)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            recommendations = body.result,
                            error = null
                        )
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "Không tải được gợi ý"
                        )
                    }
                } else {
                    val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = apiErr?.message ?: "Lỗi máy chủ"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }

    fun triggerFullPipeline(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.triggerFullPipeline()
                if (response.isSuccessful && response.body()?.code == 1000) {
                    onSuccess(response.body()?.result ?: "Pipeline đã chạy xong")
                } else {
                    onError("Trigger thất bại")
                }
            } catch (e: Exception) {
                onError("Lỗi kết nối: ${e.message}")
            }
        }
    }

    data class RecommendationState(
        val isLoading: Boolean = false,
        val recommendations: RecommendationResponse? = null,
        val error: String? = null
    )
}
