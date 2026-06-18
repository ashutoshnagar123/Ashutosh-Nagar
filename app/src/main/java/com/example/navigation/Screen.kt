package com.example.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Chat : Screen("chat", "Chat", Icons.Default.Face)
    object Projects : Screen("projects", "Tasks", Icons.AutoMirrored.Filled.List)
    object Invoices : Screen("invoices", "Bills", Icons.Default.ShoppingCart)
    object History : Screen("history")
    object Settings : Screen("settings")
    
    object AiTool : Screen("tool/{toolId}") {
        fun createRoute(toolId: String) = "tool/$toolId"
    }
}

