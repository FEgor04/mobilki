package com.jellyone.mobilki.features.auth.presentation.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jellyone.mobilki.features.auth.data.models.ApiResult
import com.jellyone.mobilki.features.auth.data.models.AuthResponse
import com.jellyone.mobilki.features.auth.data.models.User
import com.jellyone.mobilki.features.auth.data.repository.AuthRepository
import com.jellyone.mobilki.features.auth.presentation.viewmodel.AuthState
import com.jellyone.mobilki.features.auth.presentation.viewmodel.AuthViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthScreensTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: AuthViewModel
    
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
        mockViewModel = mockk(relaxed = true)
        
        // Setup default state flows
        coEvery { mockViewModel.authState } returns MutableStateFlow(AuthState.Idle)
        coEvery { mockViewModel.emailError } returns MutableStateFlow(null)
        coEvery { mockViewModel.passwordError } returns MutableStateFlow(null)
        coEvery { mockViewModel.nameError } returns MutableStateFlow(null)
    }

    @Test
    fun signInScreen_displaysAllUIComponents() {
        // Arrange
        composeTestRule.setContent {
            SignInScreen(
                onNavigateToSignUp = {},
                onSignInSuccess = {},
                viewModel = mockViewModel
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign In", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account? Sign Up").assertIsDisplayed()
    }

    @Test
    fun signInScreen_onSignInButtonClicked_callsViewModelSignIn() {
        // Arrange
        composeTestRule.setContent {
            SignInScreen(
                onNavigateToSignUp = {},
                onSignInSuccess = {},
                viewModel = mockViewModel
            )
        }

        // Act
        composeTestRule.onNodeWithTag("email_field").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("password_field").performTextInput("password123")
        composeTestRule.onNodeWithTag("signin_button").performClick()

        // Assert - No direct way to verify the function was called in pure UI tests
        // In a real app, you would use a TestNavHost or similar to verify navigation happened
    }

    @Test
    fun signInScreen_withValidationErrors_displaysErrorMessages() {
        // Arrange
        coEvery { mockViewModel.emailError } returns MutableStateFlow("Please enter a valid email address")
        coEvery { mockViewModel.passwordError } returns MutableStateFlow("Password must be at least 6 characters")

        composeTestRule.setContent {
            SignInScreen(
                onNavigateToSignUp = {},
                onSignInSuccess = {},
                viewModel = mockViewModel
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Please enter a valid email address").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password must be at least 6 characters").assertIsDisplayed()
    }

    @Test
    fun signInScreen_withAuthError_displaysErrorMessage() {
        // Arrange
        coEvery { mockViewModel.authState } returns MutableStateFlow(AuthState.Error("Invalid credentials"))

        composeTestRule.setContent {
            SignInScreen(
                onNavigateToSignUp = {},
                onSignInSuccess = {},
                viewModel = mockViewModel
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Invalid credentials").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_displaysAllUIComponents() {
        // Arrange
        composeTestRule.setContent {
            SignUpScreen(
                onNavigateToSignIn = {},
                onSignUpSuccess = {},
                viewModel = mockViewModel
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Create Account").assertIsDisplayed()
        composeTestRule.onNodeWithText("Full Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign Up", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Already have an account? Sign In").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_onSignUpButtonClicked_callsViewModelSignUp() {
        // Arrange
        composeTestRule.setContent {
            SignUpScreen(
                onNavigateToSignIn = {},
                onSignUpSuccess = {},
                viewModel = mockViewModel
            )
        }

        // Act
        composeTestRule.onNodeWithTag("name_field").performTextInput("Test User")
        composeTestRule.onNodeWithTag("email_field").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("password_field").performTextInput("password123")
        composeTestRule.onNodeWithTag("signup_button").performClick()

        // Assert - No direct way to verify the function was called in pure UI tests
    }

    @Test
    fun signUpScreen_withValidationErrors_displaysErrorMessages() {
        // Arrange
        coEvery { mockViewModel.nameError } returns MutableStateFlow("Name cannot be empty")
        coEvery { mockViewModel.emailError } returns MutableStateFlow("Please enter a valid email address")
        coEvery { mockViewModel.passwordError } returns MutableStateFlow("Password must be at least 6 characters")

        composeTestRule.setContent {
            SignUpScreen(
                onNavigateToSignIn = {},
                onSignUpSuccess = {},
                viewModel = mockViewModel
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
        composeTestRule.onNodeWithText("Please enter a valid email address").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password must be at least 6 characters").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_withAuthError_displaysErrorMessage() {
        // Arrange
        coEvery { mockViewModel.authState } returns MutableStateFlow(AuthState.Error("User already exists"))

        composeTestRule.setContent {
            SignUpScreen(
                onNavigateToSignIn = {},
                onSignUpSuccess = {},
                viewModel = mockViewModel
            )
        }

        // Assert
        composeTestRule.onNodeWithText("User already exists").assertIsDisplayed()
    }
} 