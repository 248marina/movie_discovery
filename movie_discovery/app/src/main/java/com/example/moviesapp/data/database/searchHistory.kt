package com.example.moviesapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class searchHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val movieId: Int,
    val movieTitle: String,
    val posterPath: String?,
    val releaseDate: String?,
    val overview: String?,
    val addedToFav: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val showInHistory: Boolean = true,
    val voteAverage: Double?
)