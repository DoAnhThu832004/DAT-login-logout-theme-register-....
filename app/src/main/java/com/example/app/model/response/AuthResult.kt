package com.example.app.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthResult(
    val token: String?,
    val authenticated : Boolean
) : Parcelable
