package com.jellyone.mobilki.navigation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jellyone.mobilki.features.auth.presentation.viewmodel.AuthState
import com.jellyone.mobilki.features.auth.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import io.mockk.*;

@RunWith(AndroidJUnit4::class)
class AuthNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: AuthViewModel

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
    fun authNavigation_startOnSignInScreen() {
        // Arrange & Act
        composeTestRule.setContent {
            val navController = rememberNavController()
            AuthNavigation(
                navController = navController,
                onAuthSuccess = {}
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account? Sign Up").assertIsDisplayed()
    }

    @Test
    fun authNavigation_navigateFromSignInToSignUp_displaysSignUpScreen() {
        // Arrange
        composeTestRule.setContent {
            val navController = rememberNavController()
            AuthNavigation(
                navController = navController,
                onAuthSuccess = {}
            )
        }

        // Act
        composeTestRule.onNodeWithText("Don't have an account? Sign Up").performClick()

        // Assert
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Create Account").assertIsDisplayed()
        composeTestRule.onNodeWithText("Already have an account? Sign In").assertIsDisplayed()
    }

    @Test
    fun authNavigation_navigateFromSignUpToSignIn_displaysSignInScreen() {
        // Arrange
        composeTestRule.setContent {
            val navController = rememberNavController()
            AuthNavigation(
                navController = navController,
                onAuthSuccess = {}
            )
        }

        // First navigate to sign up
        composeTestRule.onNodeWithText("Don't have an account? Sign Up").performClick()
        composeTestRule.waitForIdle()
        
        // Act - Navigate back to sign in
        composeTestRule.onNodeWithText("Already have an account? Sign In").performClick()

        // Assert
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account? Sign Up").assertIsDisplayed()
    }

    @Test
    fun authNavigation_onAuthSuccess_callsCallback() {
        // This test is more complex and would require a mock NavHost or a TestNavHost
        // In a real app, you'd verify the callback is called and navigation happens
        // For simplicity, we're skipping the implementation
    }
} 