package com.example.moviesapp.ui.favorites

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.moviesapp.data.model.Movie
import com.example.moviesapp.viewmodel.MovieViewModel

@Composable
fun FavoriteScreen(
    viewModel: MovieViewModel,
    onBackClick: () -> Unit,
    onMovieClick: (Movie) -> Unit,
    isDarkTheme: Boolean
) {
    val databaseMovies by viewModel.allMoviesInDatabase.collectAsState()

    val favoriteMovies = databaseMovies.filter { it.addedToFav }.map { historyItem ->
        Movie(
            id = historyItem.movieId,
            title = historyItem.movieTitle,
            posterPath = historyItem.posterPath,
            releaseDate = historyItem.releaseDate,
            overview = historyItem.overview,
            backdropPath = null,
            voteAverage = historyItem.voteAverage ?: 0.0,
            popularity = null

        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                .background(if(isDarkTheme) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .background(
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(50)
                        )
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if(isDarkTheme) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary
                    )
                }

                Text(
                    text = "My Favorites",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Empty State
        if (favoriteMovies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No favorite movies yet!",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 18.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favoriteMovies) { movie ->
                    FavoriteItemCard(
                        movie = movie,
                        onRemoveClick = {
                            viewModel.toggleFavorite(movie, false)
                        },
                        onItemClick = { onMovieClick(movie) },
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteItemCard(
    movie: Movie,
    onRemoveClick: () -> Unit,
    onItemClick: () -> Unit,
    isDarkTheme: Boolean
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onItemClick() }
    ) {

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = movie.getPosterUrl(),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = movie.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    maxLines = 1
                )

                Text(
                    text = movie.releaseDate ?: "",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                val backgroundColor = if (isDarkTheme) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFA855F7),
                            Color(0xFF3B82F6)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF00E5FF),
                            Color(0xFF0055A4)
                        )
                    )
                }
                Button(
                    onClick = onRemoveClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            backgroundColor
                        )
                ) {
                    Text(
                        text = "Remove",
                        color = Color.White,
                    )
                }
            }
        }
    }
}
