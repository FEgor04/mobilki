package com.jellyone.mobilki.features.auth.data.repository

import com.jellyone.mobilki.core.network.KtorClient
import com.jellyone.mobilki.features.auth.data.models.ApiResult
import com.jellyone.mobilki.features.auth.data.models.AuthResponse
import com.jellyone.mobilki.features.auth.data.models.SignInRequest
import com.jellyone.mobilki.features.auth.data.models.SignUpRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthApiService {
    private val client = KtorClient.httpClient
    
    // Replace with your actual API base URL
    private val baseUrl = "http://127.0.0.1:8080"
    
    suspend fun signIn(email: String, password: String): ApiResult<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = client.post("$baseUrl/auth/signin") {
                contentType(ContentType.Application.Json)
                setBody(SignInRequest(email, password))
            }
            
            when (response.status) {
                HttpStatusCode.OK -> ApiResult.Success(response.body())
                HttpStatusCode.Unauthorized -> ApiResult.Error("Invalid credentials")
                else -> ApiResult.Error("Error: ${response.status.description}")
            }
        } catch (e: Exception) {
            ApiResult.Error("Network error: ${e.localizedMessage}")
        }
    }
    
    suspend fun signUp(name: String, email: String, password: String): ApiResult<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = client.post("$baseUrl/auth/signup") {
                contentType(ContentType.Application.Json)
                setBody(SignUpRequest(email, password, name))
            }
            
            when (response.status) {
                HttpStatusCode.Created, HttpStatusCode.OK -> ApiResult.Success(response.body())
                HttpStatusCode.Conflict -> ApiResult.Error("User already exists")
                else -> ApiResult.Error("Error: ${response.status.description}")
            }
        } catch (e: Exception) {
            ApiResult.Error("Network error: ${e.localizedMessage}")
        }
    }
} 