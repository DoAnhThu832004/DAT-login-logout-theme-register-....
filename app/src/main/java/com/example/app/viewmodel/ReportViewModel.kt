package com.example.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.repository.ReportRepository
import com.example.app.model.request.ReportCreationRequest
import com.example.app.model.request.ReportUpdateRequest
import com.example.app.model.response.Report
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportViewModel (
    private val repository: ReportRepository
):ViewModel() {
    private val _reportState = MutableStateFlow(ReportState())
    val reportState : StateFlow<ReportState> = _reportState.asStateFlow()

    fun getReport() {
        viewModelScope.launch {
            _reportState.value = _reportState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response = repository.getReport()
                println("thuthu")
                if (response.isSuccessful) {
                    val body = response.body()
                    if(body?.code == 1000) {
                        _reportState.value = _reportState.value.copy(
                            isLoading = false,
                            reports = body.result,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _reportState.value = _reportState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    fun createReport(targetType: String, targetId: String, issueType: String, description: String) {
        viewModelScope.launch {
            _reportState.value = _reportState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val request = ReportCreationRequest(
                    targetType = targetType,
                    targetId = targetId,
                    issueType = issueType,
                    description = description
                )
                val response = repository.createReport(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000) {
                        _reportState.value = _reportState.value.copy(
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _reportState.value = _reportState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    fun updateReport(reportId:String, status:String) {
        viewModelScope.launch {
            _reportState.value = _reportState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val request = ReportUpdateRequest(
                    status = status
                )
                val response = repository.updateReport(
                    reportId = reportId,
                    request = request
                )
                if(response.isSuccessful) {
                    val body = response.body()
                    if(body?.code == 1000 && body.result != null) {
                        val updateReport = body.result
                        val currentList = _reportState.value.reports ?: emptyList()
                        val updatedList = currentList.map {
                            if(it.id == reportId) {
                                updateReport
                            } else {
                                it
                            }
                        }
                        _reportState.value = _reportState.value.copy(
                            isLoading = false,
                            reports = updatedList,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _reportState.value = _reportState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    fun deleteReport(reportId: String) {
        viewModelScope.launch {
            _reportState.value = _reportState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response = repository.deleteReport(reportId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.code == 1000) {
                        val currentList = _reportState.value.reports ?: emptyList()
                        val updatedList = currentList.filter { it.id != reportId }
                        _reportState.value = _reportState.value.copy(
                            isLoading = false,
                            reports = updatedList,
                            error = null
                        )
                    } else {
                        _reportState.value = _reportState.value.copy(
                            isLoading = false,
                            error = "Xóa thất bại"
                        )
                    }
                } else {
                    _reportState.value = _reportState.value.copy(
                        isLoading = false,
                        error = "Lỗi từ hệ thống máy chủ"
                    )
                }
            } catch (e: Exception) {
                _reportState.value = _reportState.value.copy(
                    isLoading = false,
                    error = "Lỗi: ${e.message}"
                )
            }
        }
    }
    data class ReportState(
        val reports: List<Report>? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}