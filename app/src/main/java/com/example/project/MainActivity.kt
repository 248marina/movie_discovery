package com.example.project

import android.os.Bundle
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project.auth.AuthViewModel
import com.example.project.ui.theme.ProjectTheme
import com.google.firebase.FirebaseApp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.project.settings.SettingsScreen
import com.example.project.settings.SettingsViewModel
import com.example.project.settings.SettingsViewModelFactory
import com.example.project.settings.UserPreferencesRepository


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        val repo = UserPreferencesRepository(application)
        val viewModelFactory = SettingsViewModelFactory(repo)
        val viewModel = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]

        setContent {
            val theme by viewModel.currentTheme.collectAsState()
            val language by viewModel.currentLanguage.collectAsState()
            val isThemeDark = when (theme){
                "Light" -> false
                "Dark" -> true
                else -> isSystemInDarkTheme()
            }
            ProjectTheme(dynamicColor = false, darkTheme = isThemeDark) {
                Surface(color = MaterialTheme.colorScheme.background){
                    AuthApp(settingsViewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AuthApp(settingsViewModel: SettingsViewModel){
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val uiState = authViewModel.uiState.collectAsState().value

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            if (navController.currentDestination?.route?.startsWith("login") == true ||
                navController.currentDestination?.route?.startsWith("signup") == true) {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }else {
            if (navController.currentDestination?.route?.startsWith("login") == false &&
                navController.currentDestination?.route?.startsWith("signup") == false) {
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }
    }


    NavHost(navController, startDestination = if (uiState.isLoggedIn) "home" else "login") {
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("signup") { SignupScreen(navController, authViewModel) }
        composable("home") { HomeScreen(navController, authViewModel) }
        composable("settings") { SettingsScreen(settingsViewModel) }
    }
}