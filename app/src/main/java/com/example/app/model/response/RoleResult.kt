package com.example.app.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoleResult(
    val name: String,
    val description : String,
    val permission: List<Permissions>
): Parcelable
