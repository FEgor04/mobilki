package com.jellyone.mobilki.features.auth.data.repository

import com.jellyone.mobilki.core.network.KtorClient
import com.jellyone.mobilki.features.auth.data.models.ApiResult
import com.jellyone.mobilki.features.auth.data.models.AuthResponse
import com.jellyone.mobilki.features.auth.data.models.SignInRequest
import com.jellyone.mobilki.features.auth.data.models.SignUpRequest
import com.jellyone.mobilki.features.auth.data.models.User
import com.jellyone.mobilki.util.MainDispatcherRule
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthApiServiceTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mockEngine: MockEngine
    private lateinit var mockClient: HttpClient
    private lateinit var authApiService: AuthApiService

    private val testUser = User(
        id = "test-user-id",
        email = "test@example.com",
        name = "Test User"
    )

    private val testAuthResponse = AuthResponse(
        token = "test-token",
        user = testUser
    )

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    @Before
    fun setup() {
        // Create a mock engine that will respond to requests
        mockEngine = MockEngine { request ->
            val responseHeaders = headersOf(
                "Content-Type" to listOf(ContentType.Application.Json.toString())
            )
            
            when (request.url.encodedPath) {
                "/api/auth/signin" -> {
                    val requestBody = request.body.toByteArray().decodeToString()
                    val signInRequest = json.decodeFromString<SignInRequest>(requestBody)
                    
                    if (signInRequest.email == "test@example.com" && signInRequest.password == "password123") {
                        respond(
                            content = json.encodeToString(AuthResponse.serializer(), testAuthResponse),
                            status = HttpStatusCode.OK,
                            headers = responseHeaders
                        )
                    } else {
                        respond(
                            content = """{"error": "Invalid credentials"}""",
                            status = HttpStatusCode.Unauthorized,
                            headers = responseHeaders
                        )
                    }
                }
                "/api/auth/signup" -> {
                    val requestBody = request.body.toByteArray().decodeToString()
                    val signUpRequest = json.decodeFromString<SignUpRequest>(requestBody)
                    
                    if (signUpRequest.email == "existing@example.com") {
                        respond(
                            content = """{"error": "User already exists"}""",
                            status = HttpStatusCode.Conflict,
                            headers = responseHeaders
                        )
                    } else {
                        respond(
                            content = json.encodeToString(AuthResponse.serializer(), testAuthResponse),
                            status = HttpStatusCode.Created,
                            headers = responseHeaders
                        )
                    }
                }
                else -> respond(
                    content = """{"error": "Not Found"}""",
                    status = HttpStatusCode.NotFound,
                    headers = responseHeaders
                )
            }
        }
        
        // Create a mock Ktor client with the mock engine
        mockClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }
        
        // Mock the KtorClient object to return our mock client
        mockkObject(KtorClient)
        every { KtorClient.httpClient } returns mockClient
        
        // Create the service to test
        authApiService = AuthApiService()
        
        // Override the base URL for testing
        val field = AuthApiService::class.java.getDeclaredField("baseUrl")
        field.isAccessible = true
        field.set(authApiService, "")
    }
    
    @Test
    fun `signIn with valid credentials should return Success with auth response`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        
        // Act
        val result = authApiService.signIn(email, password)
        
        // Assert
        assert(result is ApiResult.Success)
        val successResult = result as ApiResult.Success
        assertEquals(testAuthResponse.token, successResult.data.token)
        assertEquals(testAuthResponse.user.id, successResult.data.user.id)
        assertEquals(testAuthResponse.user.email, successResult.data.user.email)
        assertEquals(testAuthResponse.user.name, successResult.data.user.name)
    }
    
    @Test
    fun `signIn with invalid credentials should return Error`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "wrong-password"
        
        // Act
        val result = authApiService.signIn(email, password)
        
        // Assert
        assert(result is ApiResult.Error)
        val errorResult = result as ApiResult.Error
        assertEquals("Invalid credentials", errorResult.message)
    }
    
    @Test
    fun `signUp with new email should return Success with auth response`() = runTest {
        // Arrange
        val name = "Test User"
        val email = "new@example.com"
        val password = "password123"
        
        // Act
        val result = authApiService.signUp(name, email, password)
        
        // Assert
        assert(result is ApiResult.Success)
        val successResult = result as ApiResult.Success
        assertEquals(testAuthResponse.token, successResult.data.token)
        assertEquals(testAuthResponse.user.id, successResult.data.user.id)
        assertEquals(testAuthResponse.user.email, successResult.data.user.email)
        assertEquals(testAuthResponse.user.name, successResult.data.user.name)
    }
    
    @Test
    fun `signUp with existing email should return Error`() = runTest {
        // Arrange
        val name = "Test User"
        val email = "existing@example.com"
        val password = "password123"
        
        // Act
        val result = authApiService.signUp(name, email, password)
        
        // Assert
        assert(result is ApiResult.Error)
        val errorResult = result as ApiResult.Error
        assertEquals("User already exists", errorResult.message)
    }
} 