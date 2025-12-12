package com.example.app.model.request
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthenticationRequest(
    val username: String,
    val password: String
) : Parcelable
