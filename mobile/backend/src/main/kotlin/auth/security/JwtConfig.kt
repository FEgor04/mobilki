package com.koji.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.koji.auth.model.User
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

fun Application.configureJWT(config: TokenConfig) {
    install(Authentication) {
        jwt {
            realm = config.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(config.audience)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}

fun generateToken(user: User, config: TokenConfig): String {
    return JWT.create()
        .withSubject(user.id)
        .withAudience(config.audience)
        .withIssuer(config.issuer)
        .withClaim("email", user.email)
        .withExpiresAt(Date(System.currentTimeMillis() + config.expiresIn))
        .sign(Algorithm.HMAC256(config.secret))
}