package com.example.moviesapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviesapp.data.repository.MovieRepository
import com.example.moviesapp.data.database.searchHistoryDao

class MovieViewModelFactory(
    private val repository: MovieRepository,
    private val searchHistoryDao: searchHistoryDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            return MovieViewModel(
                repository,
                searchHistoryDao
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
