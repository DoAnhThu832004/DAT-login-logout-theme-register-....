package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiErrorUtils
import com.example.app.model.repository.SongRepository
import com.example.app.model.response.HomeRecommendationResponse
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

    /** Gọi API cũ (song-only) — giữ nguyên để không break code cũ */
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

    /** Gọi API home mới — trả về Songs + Artists + Albums + Playlists */
    fun getHomeRecommendations(userId: String) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingHome = true, homeError = null)
            try {
                val response = repository.getHomeRecommendations(userId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000 && body.result != null) {
                        _state.value = _state.value.copy(
                            isLoadingHome = false,
                            homeRecommendation = body.result,
                            homeError = null
                        )
                    } else {
                        _state.value = _state.value.copy(
                            isLoadingHome = false,
                            homeError = "Không tải được gợi ý trang chủ"
                        )
                    }
                } else {
                    val apiErr = ApiErrorUtils.parse(response.errorBody()?.string())
                    _state.value = _state.value.copy(
                        isLoadingHome = false,
                        homeError = apiErr?.message ?: "Lỗi máy chủ"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoadingHome = false,
                    homeError = "Lỗi kết nối"
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
        // Old song-only recommendation
        val isLoading: Boolean = false,
        val recommendations: RecommendationResponse? = null,
        val error: String? = null,
        // New home recommendation (Songs + Artists + Albums + Playlists)
        val isLoadingHome: Boolean = false,
        val homeRecommendation: HomeRecommendationResponse? = null,
        val homeError: String? = null
    )
}
