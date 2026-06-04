package com.danzakuduro.snailly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danzakuduro.snailly.ui.screens.BrowserScreen
import com.danzakuduro.snailly.ui.screens.DashboardScreen
import com.danzakuduro.snailly.ui.screens.DiscoveryHubScreen
import com.danzakuduro.snailly.ui.screens.LoginScreen
import com.danzakuduro.snailly.ui.screens.ParentHistoryScreen
import com.danzakuduro.snailly.ui.screens.ParentPinScreen
import com.danzakuduro.snailly.ui.screens.RegisterScreen
import com.danzakuduro.snailly.ui.screens.TodoScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var startUrl = remember { mutableStateOf("https://www.google.com/search?q=&safe=active") }

    NavHost(navController = navController, startDestination = "login") {
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
                    navController.navigate("discovery")
                },
                onOpenBrowser = {
                    startUrl.value = "https://www.google.com/search?q=&safe=active"
                    navController.navigate("browser")
                }
            )
        }
        composable("discovery") {
            DiscoveryHubScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToUrl = { url ->
                    startUrl.value = url
                    navController.navigate("browser")
                }
            )
        }
        composable("parent_pin") {
            ParentPinScreen(
                onCorrectPin = {
                    navController.navigate("parent_history") {
                        popUpTo("parent_pin") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("parent_history") {
            ParentHistoryScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("todos") {
            TodoScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("browser") {
            BrowserScreen(
                initialUrl = startUrl.value,
                onBackToDashboard = {
                    navController.popBackStack()
                }
            )
        }
    }
}
