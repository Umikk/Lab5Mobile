package com.example.laba5.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laba5.data.datastore.SearchPreferences
import com.example.laba5.data.repository.AirportRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class SearchViewModel(
    private val repository: AirportRepository,
    private val prefs: SearchPreferences
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query

    val airports = query
        .flatMapLatest {
            if (it.isEmpty()) repository.getAllAirports()
            else repository.searchAirports(it)
        }

    val favorites = repository.getFavoriteRoutes()

    fun setQuery(newQuery: String) {
        _query.value = newQuery

        viewModelScope.launch {
            prefs.saveQuery(newQuery)
        }
    }
    fun getFlights(iata: String) = repository.getFlights(iata)
    fun loadSavedQuery() {
        viewModelScope.launch {
            prefs.lastQuery.collect {
                _query.value = it
            }
        }
    }

    fun toggleFavorite(dep: String, dest: String, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) repository.deleteFavorite(dep, dest)
            else repository.insertFavorite(dep, dest)
        }
    }
}