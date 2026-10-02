package com.example.rieti.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "theme_preferences"
)

class ThemePreferences(
    private val context: Context
) {

    companion object {
        private val DARK_THEME_KEY =
            booleanPreferencesKey("dark_theme")
    }

    val darkTheme: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }

    suspend fun saveDarkTheme(
        darkTheme: Boolean
    ) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = darkTheme
        }
    }
}