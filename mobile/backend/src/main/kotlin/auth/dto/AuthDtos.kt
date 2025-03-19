package com.koji.auth.dto

import kotlinx.serialization.Serializable

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

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserDto
)