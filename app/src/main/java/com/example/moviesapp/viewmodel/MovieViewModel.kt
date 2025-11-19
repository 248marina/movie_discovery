package com.example.moviesapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.data.model.CastItem
import com.example.moviesapp.data.model.Movie
import com.example.moviesapp.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieViewModel(private val repo: MovieRepository) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies = _movies.asStateFlow()

    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie = _selectedMovie.asStateFlow()

    private val _favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMovies = _favoriteMovies.asStateFlow()


    private val _movieCast = MutableStateFlow<List<CastItem>>(emptyList())
    val movieCast = _movieCast.asStateFlow()

    fun fetchMovies() {
        viewModelScope.launch {
            try {
                _movies.value = repo.getPopularMovies()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie

        fetchMovieCast(movie.id)
    }

    fun toggleFavorite(movie: Movie) {
        val currentFavorites = _favoriteMovies.value.toMutableList()
        if (currentFavorites.any { it.id == movie.id }) {
            currentFavorites.removeAll { it.id == movie.id }
        } else {
            currentFavorites.add(movie)
        }
        _favoriteMovies.value = currentFavorites
    }

    fun isFavorite(movie: Movie): Boolean {
        return _favoriteMovies.value.any { it.id == movie.id }
    }


    private fun fetchMovieCast(movieId: Int) {
        viewModelScope.launch {
            try {
                _movieCast.value = repo.getMovieCast(movieId)
            } catch (e: Exception) {
                e.printStackTrace()
                _movieCast.value = emptyList()
            }
        }
    }
}
