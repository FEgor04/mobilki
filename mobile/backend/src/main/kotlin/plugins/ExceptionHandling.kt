package com.koji.plugins

import com.koji.auth.dto.ApiResponse
import com.koji.exceptions.GlobalExceptions
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureExceptionHandling() {
    install(StatusPages) {
        exception<GlobalExceptions> { call, cause ->
            call.respond(
                cause.statusCode,
                ApiResponse.Error(cause.message)
            )
        }

        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse.Error("Internal Server Error: ${cause.message}")
            )
        }
    }
}