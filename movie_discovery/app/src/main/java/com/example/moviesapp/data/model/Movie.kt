package com.example.moviesapp.data.model

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int,
    val title: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    val overview: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("vote_average")
    val voteAverage: Double?,
    val popularity: Double?,
    @SerializedName("genre_ids")
    val genreIds: List<Int>? = null,


) {
    fun getPosterUrl(): String {
        return if (!posterPath.isNullOrEmpty()) {
            "https://image.tmdb.org/t/p/w500$posterPath"
        } else {
            ""
        }
    }
}
