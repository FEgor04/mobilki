package com.koji

import com.koji.DatabaseFactory
import com.koji.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    // Initialize Database
    val jdbcUrl = environment.config.property("database.jdbcUrl").getString()
    val driverClassName = environment.config.property("database.driverClassName").getString()
    val username = environment.config.property("database.username").getString()
    val password = environment.config.property("database.password").getString()

    DatabaseFactory.init(jdbcUrl, driverClassName, username, password)

    // Configure plugins
    configureRouting()
    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureExceptionHandling()
}
