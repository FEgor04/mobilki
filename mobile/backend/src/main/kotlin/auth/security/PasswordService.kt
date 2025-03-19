package com.koji.auth.security

import at.favre.lib.crypto.bcrypt.BCrypt
import com.koji.AppLogger
import java.nio.charset.StandardCharsets

class PasswordService {
    private val logger = AppLogger.getLogger(this::class.java.name)

    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.verifyer()
            .verify(
                password.toByteArray(StandardCharsets.UTF_8),
                hashedPassword.toByteArray(StandardCharsets.UTF_8)
            )
            .verified
    }
}