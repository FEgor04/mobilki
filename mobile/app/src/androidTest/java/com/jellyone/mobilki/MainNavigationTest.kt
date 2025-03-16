package com.jellyone.mobilki

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jellyone.mobilki.features.auth.data.models.User
import com.jellyone.mobilki.features.auth.presentation.viewmodel.AuthViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: AuthViewModel

    private val testUser = User(
        id = "test-user-id",
        email = "test@example.com",
        name = "Test User"
    )

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
    }

    @Test
    fun mainNavigation_whenUserIsNull_startsWithAuthFlow() {
        // Arrange
        coEvery { mockViewModel.userState } returns MutableStateFlow(null)
        
        // Act
        composeTestRule.setContent {
            MainAppContent(viewModel = mockViewModel)
        }
        
        // Assert - After some navigation setup, we should see the Sign In screen
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Sign In").assertExists()
    }

    @Test
    fun mainNavigation_whenUserIsAuthenticated_startsWithHomeScreen() {
        // Arrange
        coEvery { mockViewModel.userState } returns MutableStateFlow(testUser)
        
        // Act
        composeTestRule.setContent {
            MainAppContent(viewModel = mockViewModel)
        }
        
        // Assert - After some navigation setup, we should see the Home screen
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Fitness App").assertExists()
        composeTestRule.onNodeWithText("Sign Out").assertExists()
        composeTestRule.onNodeWithText("Welcome Test User!" ).assertExists()
    }

}