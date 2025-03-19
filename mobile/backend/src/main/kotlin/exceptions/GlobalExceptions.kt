package com.koji.exceptions

import io.ktor.http.HttpStatusCode

sealed class GlobalExceptions(
    val statusCode: HttpStatusCode,
    override val message: String
) : RuntimeException(message)

class UserAlreadyExistsException(email: String) :
    GlobalExceptions(HttpStatusCode.Conflict, "User with email $email already exists")

class InvalidCredentialsException :
    GlobalExceptions(HttpStatusCode.Unauthorized, "Invalid email or password")

class UserNotFoundException(id: String) :
    GlobalExceptions(HttpStatusCode.NotFound, "User with ID $id not found")

class DatabaseOperationException(operation: String) :
    GlobalExceptions(HttpStatusCode.InternalServerError, "Database operation failed: $operation")

class EmailFormatException(email: String) :
    GlobalExceptions(HttpStatusCode.BadRequest, "Invalid email format: $email")

class WeakPasswordException :
    GlobalExceptions(HttpStatusCode.BadRequest, "Password must be at least 6 characters long")

class WeakNameException :
    GlobalExceptions(HttpStatusCode.BadRequest, "Name must be at least 6 characters long")

class UserCreationException :
    GlobalExceptions(HttpStatusCode.InternalServerError, "Failed to create user")