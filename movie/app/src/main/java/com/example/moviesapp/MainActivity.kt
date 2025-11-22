package com.example.moviesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moviesapp.data.network.ApiService
import com.example.moviesapp.data.repository.MovieRepository
import com.example.moviesapp.data.database.AppDatabase
import com.example.moviesapp.search.InteractiveCategoryTabs
import com.example.moviesapp.search.Search
import com.example.moviesapp.search.categoryScreen
import com.example.moviesapp.ui.detail.DetailScreen
import com.example.moviesapp.ui.favorites.FavoritesScreen
import com.example.moviesapp.ui.home.HomeScreen
import com.example.moviesapp.ui.theme.MoviesAppTheme
import com.example.moviesapp.viewmodel.MovieViewModel
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

        setContent {
            MoviesAppTheme {
                val navController = rememberNavController()
                val moviesState by viewModel.movies.collectAsState()
                val favoriteMovies by viewModel.favoriteMovies.collectAsState()
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                val searchHistory by viewModel.searchHistory.collectAsState()
                val allMoviesInDatabase by viewModel.allMoviesInDatabase.collectAsState()

                Scaffold(
                    bottomBar = {
                        NavigationBar(containerColor = Color.Black) {
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
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavHost(navController = navController, startDestination = "home") {
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
                                    }
                                )
                            }

                            composable("home") {
                                HomeScreen(
                                    movies = moviesState,
                                    onMovieClick = { movie ->
                                        viewModel.selectMovie(movie)
                                        navController.navigate("detail")
                                    },
                                    navController = navController
                                )
                            }

                            composable("favorites") {
                                FavoritesScreen(
                                    favoriteMovies = favoriteMovies,
                                    onMovieClick = { movie ->
                                        viewModel.selectMovie(movie)
                                        navController.navigate("detail")
                                    }
                                )
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
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineMedium,
                                        modifier = Modifier.padding(20.dp)
                                    )

                                    InteractiveCategoryTabs(
                                        allMovies = moviesState,
                                        onFilteredMovies = { filtered ->
                                            filteredMovies = filtered
                                        }
                                    )

                                    categoryScreen(
                                        movies = filteredMovies,
                                        onMovieClick = { movie ->
                                            viewModel.selectMovie(movie)
                                            navController.navigate("detail")
                                        }
                                    )
                                }
                            }

                            composable("detail") {
                                val selectedMovie by viewModel.selectedMovie.collectAsState()
                                selectedMovie?.let { movie ->
                                    DetailScreen(
                                        movie = movie,
                                        viewModel = viewModel
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        viewModel.fetchMovies()
    }
}