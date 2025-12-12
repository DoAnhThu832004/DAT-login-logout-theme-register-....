package com.example.app.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResult(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val dob: String,
    val roles: List<RoleResult>
) : Parcelable
