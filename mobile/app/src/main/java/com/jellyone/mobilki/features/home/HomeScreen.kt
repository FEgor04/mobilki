package com.jellyone.mobilki.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jellyone.mobilki.features.auth.data.models.User
import com.jellyone.mobilki.features.auth.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSignOut: () -> Unit,
    user: User?
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fitness App") },
                actions = {
                    Button(
                        onClick = {
                            onSignOut()
                        }
                    ) {
                        Text("Sign Out")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Welcome ${user?.name ?: "User"}!",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "You are now signed in to the Fitness App",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
@Preview
fun HomeScreenPreview() {
    val user = User("1234", "test-user@email.com", "Test User")
    HomeScreen(
        onSignOut = {},
        user
    )
}