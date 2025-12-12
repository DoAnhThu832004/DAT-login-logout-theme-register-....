package com.example.app.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ApiError(
    val code: Int,
    val message: String
): Parcelable
