package com.example.modus_system.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.modus_system.data.UserPreferences
import com.example.modus_system.ui.screens.EditTransactionScreen
import com.example.modus_system.ui.screens.GoldenPathScreen
import com.example.modus_system.ui.screens.HomeScreen
import com.example.modus_system.ui.screens.IronShieldScreen
import com.example.modus_system.ui.screens.LoginScreen
import com.example.modus_system.ui.screens.RegisterScreen
import com.example.modus_system.ui.screens.SettingsScreen
import com.example.modus_system.ui.screens.SplashScreen
import com.example.modus_system.viewmodel.TransactionViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val viewModel: TransactionViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.IronShield.route) {
            IronShieldScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.GoldenPath.route) {
            GoldenPathScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = Screen.EditTransaction.route,
            arguments = listOf(
                navArgument("transactionId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getInt("transactionId")
                ?: return@composable
            EditTransactionScreen(
                navController = navController,
                viewModel = viewModel,
                transactionId = transactionId
            )
        }
    }
}