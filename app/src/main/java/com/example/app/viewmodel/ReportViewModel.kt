package com.example.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiService
import com.example.app.model.request.ReportCreationRequest
import com.example.app.model.request.ReportUpdateRequest
import com.example.app.model.response.Report
import kotlinx.coroutines.launch

class ReportViewModel (
    private val apiService: ApiService
):ViewModel() {
    private val _reportState = mutableStateOf(ReportState())
    val reportState : State<ReportState> = _reportState

    fun getReport() {
        viewModelScope.launch {
            _reportState.value = _reportState.value.copy(
                isLoading = true,
                error = null
            )
            try {
                val response = apiService.getReport()
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
                val response = apiService.createReport(request)
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
                val response = apiService.updateReport(
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
    data class ReportState(
        val reports: List<Report>? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}