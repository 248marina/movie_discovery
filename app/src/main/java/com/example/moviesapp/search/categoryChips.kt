package com.example.moviesapp.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.moviesapp.data.model.Movie
import kotlin.collections.filter
import kotlin.collections.forEachIndexed
import kotlin.collections.toList
import kotlin.to


@Composable
fun InteractiveCategoryTabs(
    allMovies: List<Movie>,
    onFilteredMovies: (List<Movie>) -> Unit
) {
    // TMDB Genre IDs
    val genres = mapOf(
        "All" to null,
        "Action" to 28,
        "Adventure" to 12,
        "Comedy" to 35,
        "Drama" to 18,
        "Fantasy" to 14,
        "Horror" to 27,
        "Romance" to 10749
    )

    var selectedIndex by remember { mutableIntStateOf(0) }
    val genreList = genres.keys.toList()

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                color = Color(0xFFE8B61F)
            )
        }) {
        genreList.forEachIndexed { index, genre ->
            Tab(
                selected = selectedIndex == index,
                onClick = {
                    selectedIndex = index
                    val genreId = genres[genre]
                    val filtered = if (genreId == null) {
                        allMovies
                    } else {
                        allMovies.filter { movie ->
                            movie.genreIds?.contains(genreId) == true
                        }
                    }
                    onFilteredMovies(filtered)
                },
                text = {
                    Text(
                        text = genre,
                        color = if (selectedIndex == index) Color(0xFFE8B61F) else Color.Gray
                    )
                }
            )
        }
    }
}

@Composable
fun categoryScreen(
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {
        items(movies) { movie ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onMovieClick(movie) }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = rememberAsyncImagePainter(movie.getPosterUrl()),
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(220.dp)
                            .fillMaxWidth()
                    )
                    Text(
                        text = movie.title,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}