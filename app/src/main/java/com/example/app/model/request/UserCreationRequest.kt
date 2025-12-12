package com.example.app.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserCreationRequest(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val dob: String
) : Parcelable
