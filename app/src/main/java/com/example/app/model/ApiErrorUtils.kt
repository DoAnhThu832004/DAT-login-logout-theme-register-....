package com.example.app.model

import com.example.app.model.response.ApiError
import com.google.gson.Gson

object ApiErrorUtils { // để phân tích (parse) phần nội dung lỗi trả về từ API.
    fun parse(errorBody: String?): ApiError? {
        if(errorBody.isNullOrEmpty()) return null
        return try {
            Gson().fromJson(errorBody, ApiError::class.java)
        } catch (e : Exception) {
            null
        }
    }
}