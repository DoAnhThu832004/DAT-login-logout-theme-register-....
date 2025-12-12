package com.example.app.model.request

data class UserUpdateRequest(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val dob: String,
    val roles: List<String>? = listOf("USER")
)
