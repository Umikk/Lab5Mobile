package com.example.laba5.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "search_prefs")

class SearchPreferences(private val context: Context) {

    companion object {
        val LAST_QUERY = stringPreferencesKey("last_query")
    }

    val lastQuery: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_QUERY] ?: ""
        }

    suspend fun saveQuery(query: String) {
        context.dataStore.edit {
            it[LAST_QUERY] = query
        }
    }
}