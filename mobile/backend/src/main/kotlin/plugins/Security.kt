package com.koji.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.koji.auth.security.TokenConfig
import com.koji.auth.security.configureJWT
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()

    val tokenConfig = TokenConfig(
        issuer = jwtIssuer,
        audience = jwtAudience,
        expiresIn = 1000L * 60L * 60L * 24L * 7L, // 1 week
        secret = jwtSecret,
        realm = jwtRealm
    )

    configureJWT(tokenConfig)
}
