package com.jellyone.mobilki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jellyone.mobilki.features.auth.presentation.viewmodel.AuthViewModel
import com.jellyone.mobilki.features.home.HomeScreen
import com.jellyone.mobilki.navigation.AuthNavigation
import com.jellyone.mobilki.navigation.Routes
import com.jellyone.mobilki.ui.theme.FitnessTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitnessTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppContent()
                }
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val userState by viewModel.userState.collectAsState()
    
    // Main app navigation
    NavHost(
        navController = navController,
        startDestination = if (userState != null) Routes.HOME else "auth"
    ) {
        // Auth graph
        composable("auth") {
            AuthNavigation(
                navController = rememberNavController(),
                onAuthSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        
        // Home screen for authenticated users
        composable(Routes.HOME) {
            HomeScreen(
                onSignOut = {
                    navController.navigate("auth") {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
    
    // Check authenticated state
    LaunchedEffect(userState) {
        if (userState == null) {
            navController.navigate("auth") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        } else {
            navController.navigate(Routes.HOME) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }
}