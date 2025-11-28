package com.example.moviesapp.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.moviesapp.data.model.Movie
import com.example.moviesapp.viewmodel.MovieViewModel

@Composable
fun DetailScreen(
    movie: Movie,
    viewModel: MovieViewModel,
    color: Color,
    onActorClick: (Int) -> Unit
) {
    // 1. Collect Data & Check Favorite State
    val cast by viewModel.movieCast.collectAsState()
    val allMoviesInDatabase by viewModel.allMoviesInDatabase.collectAsState()
    val isFavorite = allMoviesInDatabase.any { it.movieId == movie.id && it.addedToFav }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .verticalScroll(rememberScrollState())
    ) {

        // 2. Header Section: Poster Image + Favorite Button
        Box {
            Image(
                painter = rememberAsyncImagePainter(movie.getPosterUrl()),
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                contentScale = ContentScale.Crop
            )

            FloatingActionButton(
                onClick = {
                    viewModel.toggleFavorite(movie, !isFavorite)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color(0xFF1C1C1C)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }
        }

        // 3. Movie Details: Title, Rating, Overview
        Column(modifier = Modifier.padding(16.dp)) {

            Text(movie.title, color = Color.White, style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(8.dp))

            Text("⭐ ${movie.voteAverage ?: 0.0}/10", color = Color.Yellow)

            Spacer(Modifier.height(20.dp))

            Text("Overview", color = Color.White, style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(8.dp))

            movie.overview?.let { Text(it, color = Color.LightGray) }

            Spacer(Modifier.height(20.dp))

            // 4. Cast Section (Horizontal List)
            Text("Cast", color = Color.White, style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                items(cast) { actor ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onActorClick(actor.id)
                            }
                            .padding(4.dp)
                    ) {

                        Image(
                            painter = rememberAsyncImagePainter(
                                "https://image.tmdb.org/t/p/w200${actor.profilePath}"
                            ),
                            contentDescription = actor.name,
                            modifier = Modifier
                                .width(100.dp)
                                .height(140.dp)
                                .background(Color.DarkGray),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(actor.name, color = Color.White, maxLines = 1)
                        actor.character?.let { Text(it, color = Color.Gray, maxLines = 1) }
                    }
                }
            }
        }
    }
}