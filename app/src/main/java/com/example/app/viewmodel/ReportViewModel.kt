package com.example.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.ApiService
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

    data class ReportState(
        val reports: List<Report>? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}