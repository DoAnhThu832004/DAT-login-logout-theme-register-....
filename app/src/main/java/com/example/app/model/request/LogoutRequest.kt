package com.example.app.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LogoutRequest(
    val token: String
) : Parcelable
