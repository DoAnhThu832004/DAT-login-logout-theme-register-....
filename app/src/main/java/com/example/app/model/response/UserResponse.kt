package com.example.app.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(
    val code: Int,
    val message: String,
    val result: UserResult
) : Parcelable
