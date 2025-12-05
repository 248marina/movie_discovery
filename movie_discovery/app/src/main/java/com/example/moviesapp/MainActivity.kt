package com.example.moviesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moviesapp.auth.AuthViewModel
import com.example.moviesapp.ui.authentication.LoginScreen
import com.example.moviesapp.ui.authentication.SignupScreen
import com.example.moviesapp.data.database.AppDatabase
import com.example.moviesapp.data.network.ApiService
import com.example.moviesapp.data.repository.MovieRepository
import com.example.moviesapp.ui.search.InteractiveCategoryTabs
import com.example.moviesapp.ui.search.Search
import com.example.moviesapp.ui.search.categoryScreen
import com.example.moviesapp.settings.SettingsScreen
import com.example.moviesapp.settings.SettingsViewModel
import com.example.moviesapp.settings.SettingsViewModelFactory
import com.example.moviesapp.settings.UserPreferencesRepository
import com.example.moviesapp.ui.actordetails.ActorDetailsScreen
import com.example.moviesapp.ui.detail.DetailScreen
import com.example.moviesapp.ui.favorites.FavoriteScreen
import com.example.moviesapp.ui.home.HomeScreen
import com.example.moviesapp.ui.theme.MoviesAppTheme
import com.example.moviesapp.viewmodel.MovieViewModel
import com.google.firebase.FirebaseApp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        val repo = MovieRepository(api)
        val database = AppDatabase.getDatabase(this)
        val searchHistoryDao = database.searchHistoryDao()
        val viewModel = MovieViewModel(repo, searchHistoryDao)

        // Firebase Authentication
        FirebaseApp.initializeApp(this)
        // Settings
        val repoSettings = UserPreferencesRepository(application)
        val viewModelFactory = SettingsViewModelFactory(repoSettings)
        val viewModelSettings = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]
        val authViewModel = AuthViewModel()


        setContent {
            val theme by viewModelSettings.currentTheme.collectAsState()
            val isThemeDark = when (theme){
                "Light" -> false
                "فاتح" -> false
                "Dark" -> true
                "داكن" -> true
                else -> isSystemInDarkTheme()
            }
            MoviesAppTheme(dynamicColor = false, darkTheme = isThemeDark) {
                val navController = rememberNavController()
                val moviesState by viewModel.movies.collectAsState()
                val favoriteMovies by viewModel.favoriteMovies.collectAsState()
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                val searchHistory by viewModel.searchHistory.collectAsState()
                val allMoviesInDatabase by viewModel.allMoviesInDatabase.collectAsState()

                // Auth State
                val authUiState by authViewModel.uiState.collectAsState()
                val isLoggedIn = authUiState. isLoggedIn

                Scaffold(
                    bottomBar = {
                        if (isLoggedIn && (currentRoute != "signup" && currentRoute != "login")) {
                            NavigationBar(
                                containerColor = if (isThemeDark) MaterialTheme.colorScheme.surface else Color(
                                    0xE6009EC6
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                            )
                                 {
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                                        label = { Text("Home") },
                                        selected = currentRoute == "home",
                                        onClick = {
                                            navController.navigate("home") {
                                                popUpTo("home") { inclusive = true }
                                            }
                                        }
                                    )

                                    NavigationBarItem(
                                        icon = { Icon(Icons.Filled.Apps, contentDescription = "Categories") },
                                        label = { Text("Categories") },
                                        selected = currentRoute == "categories",
                                        onClick = { navController.navigate("categories") }
                                    )

                                    NavigationBarItem(
                                        icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favorites") },
                                        label = { Text("Favorites") },
                                        selected = currentRoute == "favorites",
                                        onClick = { navController.navigate("favorites") }
                                    )
                                }
                            }
                        }
                    ) { paddingValues ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            NavHost(navController = navController, startDestination = if (isLoggedIn) "home" else "login") {
                                composable("login"){
                                    LoginScreen(
                                        navController = navController,
                                        viewModel = authViewModel,
                                        isDarkTheme = isThemeDark
                                    )
                                }
                                composable("signup"){
                                    SignupScreen(
                                        navController = navController,
                                        viewModel = authViewModel,
                                        isDarkTheme = isThemeDark,
                                        onBackClick = { navController.navigateUp() }
                                    )
                                }
                                composable("search") {
                                    Search(
                                        onBackClick = { navController.navigateUp() },
                                        movies = moviesState,
                                        onMovieClick = { movie ->
                                            viewModel.addMovieToHistory(movie)
                                            viewModel.selectMovie(movie)
                                            navController.navigate("detail")
                                        },
                                        searchHistory = searchHistory,
                                        allMoviesInDatabase = allMoviesInDatabase,
                                        onDeleteSearchFromHistory = { movieId ->
                                            viewModel.deleteSearchFromHistory(movieId)
                                        },
                                        onToggleFavorite = { movie, isFavorite ->
                                            viewModel.toggleFavorite(movie, isFavorite)
                                        },
                                        isDarkTheme = isThemeDark
                                    )
                                }

                                composable("home") {
                                    Box(
                                        modifier = Modifier.fillMaxSize().padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        HomeScreen(
                                            movies = moviesState,
                                            onMovieClick = { movie ->
                                                viewModel.selectMovie(movie)
                                                navController.navigate("detail")
                                            },
                                            navController = navController,
                                            isDarkTheme = isThemeDark
                                        )

                                        // logout icon button
                                        IconButton(
                                            onClick = {
                                                authViewModel.logout()
                                                navController.navigate("login"){
                                                    popUpTo(0) { inclusive = true }
                                                }
                                            },
                                            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                                contentDescription = "Logout",
                                                tint = MaterialTheme.colorScheme.onBackground
                                            )
                                        }

                                        //settings icon
                                        IconButton(
                                            onClick = { navController.navigate("settings") },
                                            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = "Settings",
                                                tint = MaterialTheme.colorScheme.onBackground
                                            )
                                        }
                                    }
                                }

                                composable(route = "favorites") {
                                    FavoriteScreen(
                                        viewModel = viewModel,
                                        onBackClick = { navController.popBackStack() },
                                        onMovieClick = { movie ->
                                            viewModel.selectMovie(movie)
                                            navController.navigate("detail")
                                        },
                                        isDarkTheme = isThemeDark
                                    )
                                }

                                composable("settings") {
                                    SettingsScreen(viewModel = viewModelSettings)
                                }

                                composable("categories") {
                                    var filteredMovies by remember { mutableStateOf(moviesState) }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Categories",
                                            color = if(isThemeDark) Color.White else Color.Black,
                                            style = MaterialTheme.typography.headlineMedium,
                                            modifier = Modifier.padding(20.dp)
                                        )

                                        InteractiveCategoryTabs(
                                            allMovies = moviesState,
                                            onFilteredMovies = { filtered ->
                                                filteredMovies = filtered
                                            },
                                            isDarkTheme = isThemeDark
                                        )

                                        categoryScreen(
                                            movies = filteredMovies,
                                            onMovieClick = { movie ->
                                                viewModel.selectMovie(movie)
                                                navController.navigate("detail")
                                            },
                                            isDarkTheme = isThemeDark
                                        )
                                    }
                                }

                                composable("detail") {
                                    val selectedMovie by viewModel.selectedMovie.collectAsState()
                                    selectedMovie?.let { movie ->
                                        DetailScreen(
                                            movie = movie,
                                            viewModel = viewModel,
                                            color = if (isThemeDark) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.background,
                                            onActorClick = { actorId ->
                                                navController.navigate("actor_details/$actorId")
                                            },
                                            isDarkTheme = isThemeDark,
                                        )
                                    }
                                }
                                composable(
                                    route = "actor_details/{actorId}",
                                    arguments = listOf(navArgument("actorId") { type = NavType.IntType })
                                ) { backStackEntry ->
                                    val actorId = backStackEntry.arguments?.getInt("actorId") ?: 0

                                    ActorDetailsScreen(
                                        actorId = actorId,
                                        viewModel = viewModel,
                                        onBackClick = { navController.popBackStack() },
                                        isDarkTheme = isThemeDark
                                    )
                                }
                            }
                        }
                    }

            }
        }

        viewModel.fetchMovies()
    }
}