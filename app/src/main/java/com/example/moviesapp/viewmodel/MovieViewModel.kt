package com.example.moviesapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.data.database.searchHistory
import com.example.moviesapp.data.database.searchHistoryDao
import com.example.moviesapp.data.model.ActorDetails
import com.example.moviesapp.data.model.CastItem
import com.example.moviesapp.data.model.Movie
import com.example.moviesapp.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieViewModel(
    private val repo: MovieRepository,
    private val historyDao: searchHistoryDao
) : ViewModel() {

    private val _selectedActor = MutableStateFlow<ActorDetails?>(null)
    val selectedActor = _selectedActor.asStateFlow()

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies = _movies.asStateFlow()

    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie = _selectedMovie.asStateFlow()

    private val _favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMovies = _favoriteMovies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _movieCast = MutableStateFlow<List<CastItem>>(emptyList())
    val movieCast = _movieCast.asStateFlow()

    val searchHistory: StateFlow<List<searchHistory>> = historyDao.getSearchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMoviesInDatabase: StateFlow<List<searchHistory>> = historyDao.getAllMoviesInDatabase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addMovieToHistory(movie: Movie, isFavorite: Boolean = false) {
        viewModelScope.launch {
            val existing = historyDao.getSearchByMovieId(movie.id)
            if (existing != null) {
                historyDao.updateSearchTimestamp(movie.id, System.currentTimeMillis())
            } else {
                historyDao.insertSearch(
                    searchHistory(
                        movieId = movie.id,
                        movieTitle = movie.title,
                        posterPath = movie.posterPath,
                        releaseDate = movie.releaseDate,
                        overview = movie.overview,
                        addedToFav = isFavorite,
                        showInHistory = true
                    )
                )
            }
        }
    }

    fun toggleFavorite(movie: Movie, isFavorite: Boolean) {
        viewModelScope.launch {
            val existing = historyDao.getSearchByMovieId(movie.id)

            if (existing != null) {
                historyDao.updateFavoriteStatus(movie.id, isFavorite)
            } else {
                historyDao.insertSearch(
                    searchHistory(
                        movieId = movie.id,
                        movieTitle = movie.title,
                        posterPath = movie.posterPath,
                        releaseDate = movie.releaseDate,
                        overview = movie.overview,
                        addedToFav = isFavorite,
                        showInHistory = false
                    )
                )
            }
        }
    }

    fun fetchMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _movies.value = repo.getPopularMovies()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
        fetchMovieCast(movie.id)
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

    fun deleteSearchFromHistory(movieId: Int) {
        viewModelScope.launch {
            historyDao.hideFromHistory(movieId)
            historyDao.deleteIfNotNeeded(movieId)
        }
    }

    fun fetchActorDetails(actorId: Int) {
        viewModelScope.launch {
            try {
                _selectedActor.value = null
                val actor = repo.getActorDetails(actorId)
                _selectedActor.value = actor
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}