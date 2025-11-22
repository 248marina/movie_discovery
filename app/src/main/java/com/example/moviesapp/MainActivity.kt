package com.example.moviesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moviesapp.data.network.ApiService
import com.example.moviesapp.data.repository.MovieRepository
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
        val viewModel = MovieViewModel(repo)

        setContent {
            MoviesAppTheme {

                val navController = rememberNavController()
                val moviesState by viewModel.movies.collectAsState()
                val favoriteMovies by viewModel.favoriteMovies.collectAsState()
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

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

                            composable("home") {
                                HomeScreen(
                                    movies = moviesState,
                                    onMovieClick = { movie ->
                                        viewModel.selectMovie(movie)
                                        navController.navigate("detail")
                                    }
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
                                Text(
                                    text = "Categories Screen",
                                    color = Color.White,
                                    modifier = Modifier.padding(20.dp)
                                )
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
