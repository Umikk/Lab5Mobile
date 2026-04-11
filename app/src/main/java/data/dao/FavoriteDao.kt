package com.example.laba5.data.dao
import com.example.laba5.data.entity.AirportEntity
import data.entity.FavoriteRoute

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.laba5.data.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite")
    fun getFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fav: FavoriteEntity)

    @Query("""
        DELETE FROM favorite 
        WHERE departure_code = :dep AND destination_code = :dest
    """)
    suspend fun delete(dep: String, dest: String)

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM favorite 
            WHERE departure_code = :dep AND destination_code = :dest
        )
        
    """)
    fun isFavorite(dep: String, dest: String): Flow<Boolean>
    @Query("""
    SELECT a.* FROM airport a
    INNER JOIN favorite f 
    ON a.iata_code = f.destination_code
    WHERE f.departure_code = :dep
""")
    fun getFavoriteDestinations(dep: String): Flow<List<AirportEntity>>

    @Query("""
    SELECT 
        a1.name AS fromName, 
        a2.name AS toName,
        f.departure_code AS departureCode,
        f.destination_code AS destinationCode
    FROM favorite f
    INNER JOIN airport a1 ON a1.iata_code = f.departure_code
    INNER JOIN airport a2 ON a2.iata_code = f.destination_code
""")
    fun getFavoriteRoutes(): Flow<List<FavoriteRoute>>
}