package com.jellyone.mobilki.features.auth.presentation.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.jellyone.mobilki.features.auth.data.models.ApiResult
import com.jellyone.mobilki.features.auth.data.models.AuthResponse
import com.jellyone.mobilki.features.auth.data.models.User
import com.jellyone.mobilki.features.auth.data.repository.AuthRepository
import com.jellyone.mobilki.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mockRepository: AuthRepository
    private lateinit var mockApplication: Application
    private lateinit var viewModel: AuthViewModel

    private val testUser = User(
        id = "test-user-id",
        email = "test@example.com",
        name = "Test User"
    )

    private val testAuthResponse = AuthResponse(
        token = "test-token",
        user = testUser
    )

    @Before
    fun setup() {
        mockRepository = mockk(relaxed = true)
        mockApplication = mockk(relaxed = true)
        
        // Setup default behavior for repository
        val userFlow = MutableStateFlow<User?>(null)
        coEvery { mockRepository.currentUser } returns userFlow
        
        // Create ViewModel with mocked dependencies
        viewModel = AuthViewModelWithMockedRepo(mockApplication, mockRepository)
    }

    @Test
    fun `signIn with valid credentials should update state to Authenticated`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        
        coEvery { mockRepository.signIn(email, password) } returns ApiResult.Success(testAuthResponse)

        // Act
        viewModel.signIn(email, password)

        // Assert
        viewModel.authState.test {
            assertEquals(AuthState.Loading, awaitItem())
            assertEquals(AuthState.Authenticated(testAuthResponse), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `signIn with invalid credentials should update state to Error`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val errorMessage = "Invalid credentials"
        
        coEvery { mockRepository.signIn(email, password) } returns ApiResult.Error(errorMessage)

        // Act
        viewModel.signIn(email, password)

        // Assert
        viewModel.authState.test {
            assertEquals(AuthState.Loading, awaitItem())
            assertEquals(AuthState.Error(errorMessage), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `signUp with valid data should update state to Authenticated`() = runTest {
        // Arrange
        val name = "Test User"
        val email = "test@example.com"
        val password = "password123"
        
        coEvery { mockRepository.signUp(name, email, password) } returns ApiResult.Success(testAuthResponse)

        // Act
        viewModel.signUp(name, email, password)

        // Assert
        viewModel.authState.test {
            assertEquals(AuthState.Loading, awaitItem())
            assertEquals(AuthState.Authenticated(testAuthResponse), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `signUp with invalid data should update state to Error`() = runTest {
        // Arrange
        val name = "Test User"
        val email = "test@example.com"
        val password = "password123"
        val errorMessage = "User already exists"
        
        coEvery { mockRepository.signUp(name, email, password) } returns ApiResult.Error(errorMessage)

        // Act
        viewModel.signUp(name, email, password)

        // Assert
        viewModel.authState.test {
            assertEquals(AuthState.Loading, awaitItem())
            assertEquals(AuthState.Error(errorMessage), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `signIn with invalid email should show validation error`() = runTest {
        // Arrange
        val email = "invalid-email"
        val password = "password123"

        // Act
        viewModel.signIn(email, password)

        // Assert
        viewModel.emailError.test {
            assertEquals("Please enter a valid email address", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        
        // The auth state should not change to loading because validation failed
        viewModel.authState.test {
            assertEquals(AuthState.Idle, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `signIn with short password should show validation error`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "12345" // Less than 6 characters

        // Act
        viewModel.signIn(email, password)

        // Assert
        viewModel.passwordError.test {
            assertEquals("Password must be at least 6 characters", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        
        // The auth state should not change to loading because validation failed
        viewModel.authState.test {
            assertEquals(AuthState.Idle, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `signOut should update state to SignedOut`() = runTest {
        // Act
        viewModel.signOut()

        // Assert
        viewModel.authState.test {
            assertEquals(AuthState.SignedOut, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `resetAuthState should reset all errors and state`() = runTest {
        // Arrange - Set some errors first
        viewModel.signIn("invalid-email", "12345")

        // Act
        viewModel.resetAuthState()

        // Assert
        viewModel.authState.test {
            assertEquals(AuthState.Idle, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        
        viewModel.emailError.test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        
        viewModel.passwordError.test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        
        viewModel.nameError.test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // Special ViewModel class with mocked repository for testing
    class AuthViewModelWithMockedRepo(
        application: Application,
        private val mockedRepository: AuthRepository
    ) : AuthViewModel(application) {
        override fun createRepository() = mockedRepository
    }
} 