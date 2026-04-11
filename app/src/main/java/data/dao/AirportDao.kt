package com.example.laba5.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.laba5.data.entity.AirportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {

    @Query("""
        SELECT * FROM airport 
        WHERE iata_code LIKE '%' || :query || '%'
        OR name LIKE '%' || :query || '%'
        ORDER BY passengers DESC
    """)
    fun searchAirports(query: String): Flow<List<AirportEntity>>

    @Query("""
        SELECT * FROM airport 
        WHERE iata_code != :code
        ORDER BY passengers DESC
    """)
    fun getFlights(code: String): Flow<List<AirportEntity>>

    @Query("SELECT * FROM airport ORDER BY passengers DESC")
    fun getAllAirports(): Flow<List<AirportEntity>>
}