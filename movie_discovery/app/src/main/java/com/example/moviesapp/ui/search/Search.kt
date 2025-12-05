package com.example.moviesapp.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.moviesapp.data.database.searchHistory
import com.example.moviesapp.data.model.Movie
import kotlinx.coroutines.delay

@Composable
fun Search(
    onBackClick: () -> Unit,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    searchHistory: List<searchHistory>,
    allMoviesInDatabase: List<searchHistory>,
    onDeleteSearchFromHistory: (Int) -> Unit,
    onToggleFavorite: (Movie, Boolean) -> Unit,
    isDarkTheme: Boolean
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    val filteredMovies = remember(searchQuery, movies) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            movies.filter { it.title.contains(searchQuery, ignoreCase = true) }
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Search Movies",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Enter movie name...") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .focusRequester(focusRequester),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (isDarkTheme) Color(0x597A7575) else Color(0xFFE5E5E5),
                unfocusedContainerColor = if (isDarkTheme) Color(0x597A7575) else Color(0xFFE5E5E5),
                cursorColor =  if (isDarkTheme) MaterialTheme.colorScheme.primary else Color(0xE6009EC6),
                focusedIndicatorColor =  if (isDarkTheme) MaterialTheme.colorScheme.primary else Color(0xE6009EC6),
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        )

        if (searchQuery.isBlank()) {
            // Show search history
            if (searchHistory.isNotEmpty()) {
                Text(
                    text = "Recently Viewed",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(searchHistory) { history ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDarkTheme) {
                                    Color(0xFF2F2B34)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    val movie = Movie(
                                        id = history.movieId,
                                        title = history.movieTitle,
                                        posterPath = history.posterPath,
                                        releaseDate = history.releaseDate,
                                        overview = history.overview,
                                        backdropPath = "",
                                       voteAverage = history.voteAverage ?: 0.0,
                                        popularity = 0.0
                                    )
                                    onMovieClick(movie)
                                },
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(8.dp)) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            "https://image.tmdb.org/t/p/w500${history.posterPath}"
                                        ),
                                        contentDescription = history.movieTitle,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .width(80.dp)
                                            .height(120.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                    Column(
                                        modifier = Modifier
                                            .padding(start = 12.dp)
                                            .weight(1f)
                                    ) {
                                        Text(
                                            text = history.movieTitle,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteSearchFromHistory(history.movieId) }
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Delete",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Favorite button at bottom
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val movie = Movie(
                                                id = history.movieId,
                                                title = history.movieTitle,
                                                posterPath = history.posterPath,
                                                releaseDate = history.releaseDate,
                                                overview = history.overview,
                                                backdropPath = "",
                                                voteAverage = history.voteAverage ?: 0.0,
                                                popularity = 0.0
                                            )
                                            onToggleFavorite(movie, !history.addedToFav)
                                        }
                                        .padding(start = 8.dp, bottom = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (history.addedToFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = if (history.addedToFav) "Added to favorites" else "Add to favorites",
                                        tint = if (history.addedToFav) Color.Red else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (history.addedToFav) "Added to favorites" else "Add to favorites",
                                        color = if (history.addedToFav) Color.Red else MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "No recently viewed movies",
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            // Show search results
            Text(
                text = "Found ${filteredMovies.size} movies",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(filteredMovies) { movie ->
                    // Check ALL movies in database for favorite status
                    val movieInDb = allMoviesInDatabase.find { it.movieId == movie.id }
                    val isFavorite = movieInDb?.addedToFav ?: false

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) {
                                Color(0xFF2F2B34)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable { onMovieClick(movie) }
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(movie.getPosterUrl()),
                                    contentDescription = movie.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                Column(
                                    modifier = Modifier
                                        .padding(start = 12.dp)
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = movie.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    movie.overview?.let {
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 3,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onToggleFavorite(movie, !isFavorite)
                                    }
                                    .padding(start = 8.dp, bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (isFavorite) "Added to favorites" else "Add to favorites",
                                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isFavorite) "Added to favorites" else "Add to favorites",
                                    color = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
