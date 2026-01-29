package com.grocart.first.data;
data class UserResponse(
    val id: Long, // 👈 MySQL ki unique ID
    val username: String,
    val email: String
)