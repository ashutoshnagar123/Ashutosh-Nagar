package com.example.navigation

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.example.*
import com.example.auth.*
import com.example.tools.*

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

@Composable
fun AppNavigation(
    windowSizeClass: WindowSizeClass,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    when (authState) {
        AuthStatus.LOADING -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        AuthStatus.AUTHENTICATED, AuthStatus.GUEST -> {
            MainScreen(
                windowSizeClass = windowSizeClass,
                onLogout = { authViewModel.logout() }
            )
        }
        AuthStatus.UNAUTHENTICATED -> {
            AuthNavHost(authViewModel)
        }
    }
}

@Composable
fun AuthNavHost(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToSignUp = { navController.navigate(Screen.Signup.route) },
                onNavigateToForgot = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }
        composable(Screen.Signup.route) {
            SignUpScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Chat,
        Screen.Projects,
        Screen.Invoices
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        bottomNavItems.forEach { screen ->
                            val selected = currentDestination?.route == screen.route
                            NavigationBarItem(
                                icon = { 
                                    screen.icon?.let { icon ->
                                        Icon(icon, contentDescription = screen.title) 
                                    }
                                },
                                label = { 
                                    screen.title?.let { title ->
                                        Text(title)
                                    }
                                },
                                selected = selected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                MainContent(navController, innerPadding, onLogout)
            }
        }
        WindowWidthSizeClass.Medium -> {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.route == screen.route
                        NavigationRailItem(
                            icon = { 
                                screen.icon?.let { icon ->
                                    Icon(icon, contentDescription = screen.title) 
                                }
                            },
                            label = { 
                                screen.title?.let { title ->
                                    Text(title)
                                }
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
                MainContent(navController, PaddingValues(0.dp), onLogout)
            }
        }
        else -> { // Expanded
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet {
                        Text(
                            text = "FreelanceAI",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                        bottomNavItems.forEach { screen ->
                            val selected = currentDestination?.route == screen.route
                            NavigationDrawerItem(
                                icon = { 
                                    screen.icon?.let { icon ->
                                        Icon(icon, contentDescription = screen.title) 
                                    }
                                },
                                label = { 
                                    screen.title?.let { title ->
                                        Text(title)
                                    }
                                },
                                selected = selected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            ) {
                MainContent(navController, PaddingValues(0.dp), onLogout)
            }
        }
    }
}

@Composable
fun MainContent(
    navController: androidx.navigation.NavHostController,
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.padding(innerPadding).fillMaxSize()
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onLogout = onLogout,
                onNavigateToTool = { toolId -> navController.navigate(Screen.AiTool.createRoute(toolId)) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToProjects = { 
                    navController.navigate(Screen.Projects.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(Screen.Chat.route) { ChatScreen() }
        composable(Screen.Projects.route) { ProjectManagerScreen() }
        composable(Screen.Invoices.route) { InvoiceScreen() }
        composable(Screen.History.route) { HistoryScreen() }
        composable(Screen.Settings.route) { SettingsScreen() }
        
        composable(
            route = Screen.AiTool.route
        ) { backStackEntry ->
            val toolId = backStackEntry.arguments?.getString("toolId") ?: ""
            
            val (title, hint) = when (toolId) {
                "proposal" -> Pair("Proposal Writer", "Enter client and project details...")
                "content" -> Pair("Content Writer", "Enter topic and tone...")
                "email" -> Pair("Email Writer", "Who are you emailing and why?")
                "resume" -> Pair("Resume Builder", "Enter your skills and target role...")
                "code" -> Pair("Code Generator", "Describe the code you need...")
                else -> Pair("AI Assistant", "Ask me anything...")
            }
            
            val viewModel: AiToolViewModel = hiltViewModel()
            
            AiToolScreen(
                title = title,
                hintText = hint,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
