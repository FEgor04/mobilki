package com.koji.auth.model

data class User(
    val id: String,
    val email: String,
    val password: String,
    val name: String
)