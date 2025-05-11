package com.example.savor_recipe_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savor_recipe_app.auth.AuthViewModel
import com.example.savor_recipe_app.auth.AuthState
import com.example.savor_recipe_app.data.UserRepository
import com.example.savor_recipe_app.data.UserProfile

@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    
    val authState by authViewModel.authState.collectAsState()
    val userRepository = remember { UserRepository() }
    
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            // Create user profile after successful signup
            authViewModel.currentUser.value?.let { user ->
                val profile = UserProfile(
                    name = name,
                    email = email
                )
                userRepository.createUserProfile(user, profile)
                onSignUpSuccess()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        Button(
            onClick = { authViewModel.signUp(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Sign Up")
        }

        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Already have an account? Login")
        }

        if (authState is AuthState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        }

        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
} 