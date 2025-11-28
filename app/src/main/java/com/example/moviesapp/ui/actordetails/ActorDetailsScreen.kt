package com.example.moviesapp.ui.actordetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.moviesapp.ui.theme.CardDarkGray
import com.example.moviesapp.ui.theme.MainBlack
import com.example.moviesapp.ui.theme.YellowAccent
import com.example.moviesapp.viewmodel.MovieViewModel

@Composable
fun ActorDetailsScreen(
    actorId: Int,
    viewModel: MovieViewModel,
    onBackClick: () -> Unit
) {
    LaunchedEffect(actorId) {
        viewModel.fetchActorDetails(actorId)
    }

    // Watch for data changes from ViewModel
    val actor by viewModel.selectedActor.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBlack)
    ) {

        // Scrollable content column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            // Check if data is still loading
            if (actor == null) {
                // Show loading spinner in the center
                Box(modifier = Modifier.fillMaxWidth().height(400.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = YellowAccent)
                }
            } else {
                val currentActor = actor!!

                // Actor Image (Big and Circular)
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = "https://image.tmdb.org/t/p/w500${currentActor.profilePath}"
                        ),
                        contentDescription = currentActor.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(280.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actor Name
                Text(
                    text = currentActor.name,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Personal Info (Born, Place)
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp)
                ) {
                    ActorInfoRow(label = "Born:", value = currentActor.birthday ?: "N/A")
                    Spacer(modifier = Modifier.height(8.dp))
                    ActorInfoRow(label = "Place:", value = currentActor.placeOfBirth ?: "N/A")
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Section Title
                Text(
                    text = "Biography",
                    color = YellowAccent,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Biography Card at the bottom
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardDarkGray),
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 500.dp) // Make it tall enough
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        // Check if bio is empty
                        val bioText = if (currentActor.biography.isNotEmpty()) {
                            currentActor.biography
                        } else {
                            "No biography available for this actor."
                        }

                        // Display Bio text
                        Text(
                            text = bioText,
                            color = Color.LightGray,
                            fontSize = 18.sp,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }
        }

        // Fixed Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(50.dp)
                .background(Color.DarkGray.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}

@Composable
fun ActorInfoRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 16.sp
        )
    }
}