package com.example.moviesapp.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface searchHistoryDao {

    @Query("SELECT * FROM search_history WHERE showInHistory = 1 ORDER BY timestamp DESC LIMIT 10")
    fun getSearchHistory(): Flow<List<searchHistory>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSearch(searchHistory: searchHistory)

    @Query("SELECT * FROM search_history WHERE movieId = :movieId LIMIT 1")
    suspend fun getSearchByMovieId(movieId: Int): searchHistory?


    @Query("UPDATE search_history SET timestamp = :timestamp, showInHistory = 1 WHERE movieId = :movieId")
    suspend fun updateSearchTimestamp(movieId: Int, timestamp: Long)

    @Query("UPDATE search_history SET showInHistory = 0 WHERE movieId = :movieId")
    suspend fun hideFromHistory(movieId: Int)
    @Query("DELETE FROM search_history WHERE movieId = :movieId AND addedToFav = 0 AND showInHistory = 0")
    suspend fun deleteIfNotNeeded(movieId: Int)

    @Query("DELETE FROM search_history WHERE showInHistory = 0 AND addedToFav = 0")
    suspend fun clearHistory()


    @Query("UPDATE search_history SET addedToFav = :isFavorite WHERE movieId = :movieId")
    suspend fun updateFavoriteStatus(movieId: Int, isFavorite: Boolean)

    @Query("SELECT * FROM search_history")
    fun getAllMoviesInDatabase(): Flow<List<searchHistory>>
}