package com.example.project.settings

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings_prefs")

class UserPreferencesRepository(app: Application) : AndroidViewModel(app) {
    private val dataStore = app.dataStore
    private val appThemeKey = stringPreferencesKey("app_theme")

    val themeFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[appThemeKey] ?: "System"
        }
    suspend fun saveTheme(themeValue: String) {
        dataStore.edit { preferences ->
            preferences[appThemeKey] = themeValue
        }
    }
}
