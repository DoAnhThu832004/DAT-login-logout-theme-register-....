package com.example.app.model.repository

import com.example.app.model.ApiService
import com.example.app.model.request.ReportCreationRequest
import com.example.app.model.request.ReportUpdateRequest

class ReportRepository(
    private val apiService: ApiService
) {
    suspend fun getReport() = apiService.getReport()
    suspend fun createReport(request: ReportCreationRequest) = apiService.createReport(request)
    suspend fun updateReport(reportId: String, request: ReportUpdateRequest) = apiService.updateReport(reportId, request)
}