package com.koji.auth.service

import com.koji.auth.dto.AuthResponse
import com.koji.auth.dto.SignInRequest
import com.koji.auth.dto.SignUpRequest
import com.koji.auth.dto.UserDto
import com.koji.auth.model.User
import com.koji.auth.security.PasswordService
import com.koji.auth.security.TokenConfig
import com.koji.auth.security.generateToken
import com.koji.exceptions.*
import com.koji.AppLogger
import com.koji.auth.repository.UserRepository
import java.util.*

class AuthService(
    private val userRepository: UserRepository,
    private val passwordService: PasswordService,
    private val tokenConfig: TokenConfig
) {
    private val logger = AppLogger.getLogger(this::class.java.name)

    suspend fun signUp(request: SignUpRequest): AuthResponse {
        if (!request.email.contains("@")) {
            throw EmailFormatException(request.email)
        }
        if (request.password.length < 6) {
            throw WeakPasswordException()
        }
        if (request.name.length < 6) {
            throw WeakNameException()
        }

        val existingUser = userRepository.findUserByEmail(request.email)
        if (existingUser != null) {
            logger.info("User with email ${request.email} already exists")
            throw UserAlreadyExistsException(request.email)
        }

        val hashedPassword = passwordService.hashPassword(request.password)
        val user = User(
            id = UUID.randomUUID().toString(),
            email = request.email,
            password = hashedPassword,
            name = request.name
        )

        val createdUser = userRepository.createUser(user)

        val token = generateToken(createdUser, tokenConfig)
        logger.info("User with email ${request.email} signed up successfully")

        return AuthResponse(
            token = token,
            user = UserDto(
                id = createdUser.id,
                email = createdUser.email,
                name = createdUser.name
            )
        )
    }

    suspend fun signIn(request: SignInRequest): AuthResponse {
        val user = userRepository.findUserByEmail(request.email)
            ?: throw InvalidCredentialsException()

        val isValidPassword = passwordService.verifyPassword(request.password, user.password)
        if (!isValidPassword) {
            logger.info("Invalid password attempt for user ${request.email}")
            throw InvalidCredentialsException()
        }

        val token = generateToken(user, tokenConfig)
        logger.info("User with email ${request.email} signed in successfully")

        return AuthResponse(
            token = token,
            user = UserDto(
                id = user.id,
                email = user.email,
                name = user.name
            )
        )
    }
}
