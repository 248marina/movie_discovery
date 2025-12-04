package com.example.moviesapp.ui.favorites

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.moviesapp.data.model.Movie
import com.example.moviesapp.ui.theme.CardDarkGray
import com.example.moviesapp.ui.theme.DarkBackground
import com.example.moviesapp.ui.theme.RedButton
import com.example.moviesapp.ui.theme.YellowHeader
import com.example.moviesapp.viewmodel.MovieViewModel

@Composable
fun FavoriteScreen(
    viewModel: MovieViewModel,
    onBackClick: () -> Unit,
    onMovieClick: (Movie) -> Unit
) {
    // Get live updates from the database
    val databaseMovies by viewModel.allMoviesInDatabase.collectAsState()

    // Filter only movies marked as favorite & map them to Movie object
    val favoriteMovies = databaseMovies.filter { it.addedToFav }.map { historyItem ->
        Movie(
            id = historyItem.movieId,
            title = historyItem.movieTitle,
            posterPath = historyItem.posterPath,
            releaseDate = historyItem.releaseDate,
            overview = historyItem.overview,
            backdropPath = null,
            voteAverage = null,
            popularity = null
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //  Custom Header with curved bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                .background(YellowHeader)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Back Button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .background(Color.Black.copy(alpha = 0.2f), shape = RoundedCornerShape(50))
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }

                // Page Title
                Text(
                    text = "My Favorites",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        //Show empty state if list is empty, otherwise show list
        if (favoriteMovies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No favorite movies yet!",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            //List of favorite movies
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favoriteMovies) { movie ->
                    FavoriteItemCard(
                        movie = movie,
                        onRemoveClick = {
                            // Update DB to remove favorite
                            viewModel.toggleFavorite(movie, false)
                        },
                        onItemClick = { onMovieClick(movie) }
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
    onItemClick: () -> Unit
) {
    //Clickable Card Container
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardDarkGray),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onItemClick() } // Open details on click
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Movie Poster
            AsyncImage(
                model = movie.getPosterUrl(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            //Movie Info Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                // Release Date
                Text(
                    text = movie.releaseDate ?: "",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                //Remove Button
                Button(
                    onClick = onRemoveClick,
                    colors = ButtonDefaults.buttonColors(containerColor = RedButton),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(40.dp)
                ) {
                    Text(text = "Remove", color = Color.White)
                }
            }
        }
    }
}