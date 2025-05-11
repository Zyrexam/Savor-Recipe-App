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

@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    
    val authState by authViewModel.authState.collectAsState()
    
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
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
            text = "Welcome to Savor",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
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
                .padding(bottom = 8.dp)
        )

        TextButton(
            onClick = { showForgotPasswordDialog = true },
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 24.dp)
        ) {
            Text("Forgot Password?")
        }

        Button(
            onClick = { authViewModel.signIn(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Login")
        }

        TextButton(
            onClick = onNavigateToSignUp,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Don't have an account? Sign up")
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

    if (showForgotPasswordDialog) {
        var resetEmail by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Reset Password") },
            text = {
                Column {
                    Text("Enter your email address to receive a password reset link.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.resetPassword(resetEmail)
                        showForgotPasswordDialog = false
                    }
                ) {
                    Text("Send Reset Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 