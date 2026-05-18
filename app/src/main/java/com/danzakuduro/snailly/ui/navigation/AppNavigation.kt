package com.danzakuduro.snailly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danzakuduro.snailly.ui.screens.DashboardScreen
import com.danzakuduro.snailly.ui.screens.LoginScreen
import com.danzakuduro.snailly.ui.screens.RegisterScreen
import com.danzakuduro.snailly.ui.screens.TodoScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "register") {
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { 
                    navController.navigate("register") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onLoginSuccess = { 
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    } 
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { 
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    } 
                },
                onRegisterSuccess = { 
                    // Setelah registrasi, user harus konfirmasi email, 
                    // jadi kita arahkan ke halaman Login agar mereka bisa masuk setelah konfirmasi.
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    } 
                }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                onLogout = { 
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    } 
                },
                onViewTodos = {
                    navController.navigate("todos")
                }
            )
        }
        composable("todos") {
            TodoScreen()
        }
    }
}
