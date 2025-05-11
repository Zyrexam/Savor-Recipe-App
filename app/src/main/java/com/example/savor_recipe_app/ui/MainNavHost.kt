package com.example.savor_recipe_app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.savor_recipe_app.auth.AuthViewModel
import com.example.savor_recipe_app.ui.screens.LoginScreen
import com.example.savor_recipe_app.ui.screens.SignUpScreen
import com.example.savor_recipe_app.ui.screens.ProfileScreen

@Composable
fun MainNavHost(authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate("signup") },
                onLoginSuccess = {
                    navController.navigate("profile") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }
        composable("signup") {
            SignUpScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate("profile") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }
        composable("profile") {
            ProfileScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                },
                authViewModel = authViewModel
            )
        }
    }
} 