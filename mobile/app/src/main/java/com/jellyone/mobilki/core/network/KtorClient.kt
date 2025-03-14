package com.jellyone.mobilki.core.network

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient {
    private const val TIME_OUT = 60_000
    private const val TAG = "KtorClient"
    
    val httpClient = HttpClient(Android) {
        // Configure timeout
        install(HttpTimeout) {
            requestTimeoutMillis = TIME_OUT.toLong()
            connectTimeoutMillis = TIME_OUT.toLong()
            socketTimeoutMillis = TIME_OUT.toLong()
        }
        
        // Install ContentNegotiation
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        
        // Logging
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d(TAG, message)
                }
            }
            level = LogLevel.ALL
        }
        
        // Default request configuration
        defaultRequest {
            // Base URL can be set here if it's constant
            // url("https://api.example.com/")
        }
    }
} 