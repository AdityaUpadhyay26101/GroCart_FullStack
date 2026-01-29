package com.grocart.first.network

import kotlinx.serialization.Serializable

// Isse network package mein daalein
@Serializable
data class UserSignupRequest(
    val username: String,
    val email: String,
    val password: String
)