package com.example.moviesapp.data.network

import com.example.moviesapp.data.model.MovieResponse
import com.example.moviesapp.data.model.CastResponse
import com.example.moviesapp.BuildConfig
import com.example.moviesapp.data.model.ActorDetails
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("person/{person_id}")
    suspend fun getActorDetails(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String = BuildConfig.TMDB_API_KEY
    ): ActorDetails

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
