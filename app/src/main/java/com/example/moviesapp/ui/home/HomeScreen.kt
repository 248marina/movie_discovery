package com.example.moviesapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.moviesapp.data.model.Movie
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun HomeScreen(movies: List<Movie>, onMovieClick: (Movie) -> Unit) {

    val pagerState = rememberPagerState(pageCount = { movies.size })

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {

        Text(
            text = "Popular Movies",
            modifier = Modifier.padding(bottom = 24.dp),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .height(420.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 80.dp),
            pageSpacing = 16.dp
        ) { page ->

            val movie = movies[page]

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .clickable { onMovieClick(movie) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box {
                    Image(
                        painter = rememberAsyncImagePainter(movie.getPosterUrl()),
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay for better text visibility
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    ),
                                    startY = 200f
                                )
                            )
                    )

                    // Movie info at bottom
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = movie.title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "⭐ ${movie.voteAverage}/10",
                                color = Color(0xFFFFD700),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = movie.releaseDate?.take(4) ?: "",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        DotsIndicator(
            totalDots = movies.size,
            selectedIndex = pagerState.currentPage,
            selectedColor = Color.Red,
            unSelectedColor = Color.Gray
        )
    }
}

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color,
    unSelectedColor: Color
) {
    val maxVisibleDots = 5

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (totalDots <= maxVisibleDots) {
            // Show all dots if total is less than max
            for (i in 0 until totalDots) {
                Dot(
                    isSelected = i == selectedIndex,
                    selectedColor = selectedColor,
                    unSelectedColor = unSelectedColor
                )
            }
        } else {
            // Smart pagination for many dots
            val start = when {
                selectedIndex < 2 -> 0
                selectedIndex > totalDots - 3 -> totalDots - maxVisibleDots
                else -> selectedIndex - 2
            }
            val end = (start + maxVisibleDots).coerceAtMost(totalDots)

            // Show left indicator if not at start
            if (start > 0) {
                Text(
                    text = "‹",
                    color = unSelectedColor,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Show visible dots
            for (i in start until end) {
                Dot(
                    isSelected = i == selectedIndex,
                    selectedColor = selectedColor,
                    unSelectedColor = unSelectedColor
                )
            }

            // Show right indicator if not at end
            if (end < totalDots) {
                Text(
                    text = "›",
                    color = unSelectedColor,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun Dot(
    isSelected: Boolean,
    selectedColor: Color,
    unSelectedColor: Color
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .size(
                width = if (isSelected) 24.dp else 8.dp,
                height = 8.dp
            )
            .background(
                color = if (isSelected) selectedColor else unSelectedColor,
                shape = if (isSelected) MaterialTheme.shapes.small else MaterialTheme.shapes.extraSmall
            )
    )
}