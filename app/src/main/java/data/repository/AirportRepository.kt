package com.example.laba5.data.repository

import com.example.laba5.data.dao.AirportDao
import com.example.laba5.data.dao.FavoriteDao
import com.example.laba5.data.entity.FavoriteEntity

class AirportRepository(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao
) {

    fun getAllAirports() = airportDao.getAllAirports()

    fun searchAirports(query: String) =
        airportDao.searchAirports("%$query%")

    fun getFlights(code: String) = airportDao.getFlights(code)

    fun getFavorites() = favoriteDao.getFavorites()

    fun getFavoriteRoutes() = favoriteDao.getFavoriteRoutes()

    suspend fun insertFavorite(dep: String, dest: String) {
        favoriteDao.insert(
            FavoriteEntity(
                departureCode = dep,
                destinationCode = dest
            )
        )
    }

    suspend fun deleteFavorite(dep: String, dest: String) {
        favoriteDao.delete(dep, dest)
    }
}