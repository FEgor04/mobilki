package com.jellyone.mobilki.features.auth.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import app.cash.turbine.test
import com.jellyone.mobilki.features.auth.data.models.ApiResult
import com.jellyone.mobilki.features.auth.data.models.AuthResponse
import com.jellyone.mobilki.features.auth.data.models.User
import com.jellyone.mobilki.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var authApiService: AuthApiService
    private lateinit var context: Context
    private lateinit var authRepository: AuthRepository

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
        val dataStoreFile = tempFolder.newFile("test_datastore.preferences_pb")
        
        testDataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { dataStoreFile }
        )
        
        authApiService = mockk(relaxed = true)
        context = mockk {
            every { applicationContext } returns this
            every { dataStore } returns testDataStore
        }
        
        authRepository = AuthRepository(context, authApiService)
    }
    
    @Test
    fun `signIn success should save auth data to DataStore`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        
        coEvery { 
            authApiService.signIn(email, password) 
        } returns ApiResult.Success(testAuthResponse)

        // Act
        val result = authRepository.signIn(email, password)

        // Assert
        assertEquals(ApiResult.Success(testAuthResponse), result)
        
        // Check if token and user info were saved
        authRepository.currentUser.test {
            val savedUser = awaitItem()
            assertEquals(testUser.id, savedUser?.id)
            assertEquals(testUser.email, savedUser?.email)
            assertEquals(testUser.name, savedUser?.name)
            cancelAndIgnoreRemainingEvents()
        }
        
        authRepository.authToken.test {
            assertEquals(testAuthResponse.token, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `signIn failure should not save auth data`() = runTest {
        // Arrange
        val email = "test@example.com"
        val password = "wrong-password"
        val errorMessage = "Invalid credentials"
        
        coEvery { 
            authApiService.signIn(email, password) 
        } returns ApiResult.Error(errorMessage)

        // Act
        val result = authRepository.signIn(email, password)

        // Assert
        assertEquals(ApiResult.Error(errorMessage), result)
        
        // Check that no user or token were saved
        authRepository.currentUser.test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        
        authRepository.authToken.test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `signUp success should save auth data to DataStore`() = runTest {
        // Arrange
        val name = "Test User"
        val email = "test@example.com"
        val password = "password123"
        
        coEvery { 
            authApiService.signUp(name, email, password) 
        } returns ApiResult.Success(testAuthResponse)

        // Act
        val result = authRepository.signUp(name, email, password)

        // Assert
        assertEquals(ApiResult.Success(testAuthResponse), result)
        
        // Check if token and user info were saved
        authRepository.currentUser.test {
            val savedUser = awaitItem()
            assertEquals(testUser.id, savedUser?.id)
            assertEquals(testUser.email, savedUser?.email)
            assertEquals(testUser.name, savedUser?.name)
            cancelAndIgnoreRemainingEvents()
        }
        
        authRepository.authToken.test {
            assertEquals(testAuthResponse.token, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `signOut should clear auth data from DataStore`() = runTest {
        // Arrange - First sign in to save data
        coEvery { 
            authApiService.signIn("test@example.com", "password123") 
        } returns ApiResult.Success(testAuthResponse)
        
        authRepository.signIn("test@example.com", "password123")
        
        // Verify data was saved
        authRepository.authToken.test {
            assertEquals(testAuthResponse.token, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        // Act - Sign out
        authRepository.signOut()

        // Assert - Check that data was cleared
        authRepository.currentUser.test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        
        authRepository.authToken.test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `isAuthenticated should return true when token exists`() = runTest {
        // Arrange - First sign in to save token
        coEvery { 
            authApiService.signIn("test@example.com", "password123") 
        } returns ApiResult.Success(testAuthResponse)
        
        authRepository.signIn("test@example.com", "password123")

        // Act & Assert
        authRepository.isAuthenticated().test {
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `isAuthenticated should return false when token doesn't exist`() = runTest {
        // Act & Assert - Without signing in
        authRepository.isAuthenticated().test {
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
} 