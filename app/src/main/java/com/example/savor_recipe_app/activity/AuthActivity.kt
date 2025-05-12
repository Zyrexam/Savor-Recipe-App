package com.example.savor_recipe_app.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savor_recipe_app.auth.AuthViewModel
import com.example.savor_recipe_app.ui.theme.SavorRecipeAppTheme
import com.example.savor_recipe_app.ui.screens.LoginScreen
import com.example.savor_recipe_app.ui.screens.SignUpScreen

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SavorRecipeAppTheme {
                AuthScreen(
                    onAuthSuccess = {
                        startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var showLogin by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (showLogin) {
            LoginScreen(
                onNavigateToSignUp = { showLogin = false },
                onLoginSuccess = onAuthSuccess,
                authViewModel = authViewModel
            )
        } else {
            SignUpScreen(
                onNavigateToLogin = { showLogin = true },
                onSignUpSuccess = onAuthSuccess,
                authViewModel = authViewModel
            )
        }
    }
} 