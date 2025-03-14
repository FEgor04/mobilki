package com.jellyone.mobilki.features.auth.data.models

import kotlinx.serialization.Serializable

// Request models
@Serializable
data class SignInRequest(
    val email: String,
    val password: String
)

@Serializable
data class SignUpRequest(
    val email: String,
    val password: String,
    val name: String
)

// Response models
@Serializable
data class AuthResponse(
    val token: String,
    val user: User
)

@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String
)

// API result wrapper
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int = 0) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
} 