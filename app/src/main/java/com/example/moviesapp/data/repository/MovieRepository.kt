package com.example.moviesapp.data.repository

import com.example.moviesapp.BuildConfig
import com.example.moviesapp.data.model.ActorDetails
import com.example.moviesapp.data.model.CastItem
import com.example.moviesapp.data.network.ApiService

class MovieRepository(private val api: ApiService) {

    suspend fun getPopularMovies() =
        api.getPopularMovies().results

    suspend fun getMovieCast(movieId: Int): List<CastItem> {
        return api.getMovieCast(movieId).cast
    }

    suspend fun getActorDetails(id: Int): ActorDetails {
        return api.getActorDetails(id, BuildConfig.TMDB_API_KEY)
    }
}
