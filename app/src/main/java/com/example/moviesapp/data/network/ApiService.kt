package com.example.moviesapp.data.network

import com.example.moviesapp.data.model.MovieResponse
import com.example.moviesapp.data.model.CastResponse
import com.example.moviesapp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCast(
        @retrofit2.http.Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): CastResponse
}
