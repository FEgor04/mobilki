package com.jellyone.mobilki.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jellyone.mobilki.features.auth.presentation.screens.SignInScreen
import com.jellyone.mobilki.features.auth.presentation.screens.SignUpScreen

object Routes {
    const val SIGN_IN = "sign_in"
    const val SIGN_UP = "sign_up"
    const val HOME = "home"
}

@Composable
fun AuthNavigation(
    navController: NavHostController,
    onAuthSuccess: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SIGN_IN
    ) {
        composable(Routes.SIGN_IN) {
            SignInScreen(
                onNavigateToSignUp = {
                    navController.navigate(Routes.SIGN_UP) {
                        popUpTo(Routes.SIGN_IN) { inclusive = true }
                    }
                },
                onSignInSuccess = onAuthSuccess
            )
        }
        
        composable(Routes.SIGN_UP) {
            SignUpScreen(
                onNavigateToSignIn = {
                    navController.navigate(Routes.SIGN_IN) {
                        popUpTo(Routes.SIGN_UP) { inclusive = true }
                    }
                },
                onSignUpSuccess = onAuthSuccess
            )
        }
    }
} 