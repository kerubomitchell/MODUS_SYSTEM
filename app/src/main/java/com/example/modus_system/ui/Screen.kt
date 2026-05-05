package com.example.modus_system.ui

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Register : Screen("register")
    object Login : Screen("login")
    object Home : Screen("home")
    object IronShield : Screen("iron_shield")
    object GoldenPath : Screen("golden_path")
    object Settings : Screen("settings")
    object EditTransaction : Screen("edit_transaction/{transactionId}") {
        fun createRoute(transactionId: Int) = "edit_transaction/$transactionId"
    }
}