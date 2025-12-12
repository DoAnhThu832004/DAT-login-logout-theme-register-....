package com.example.app.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Permissions(
    val name: String,
    val description: String
) : Parcelable
