package com.example.laba5.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.laba5.data.dao.AirportDao
import com.example.laba5.data.dao.FavoriteDao
import com.example.laba5.data.entity.AirportEntity
import com.example.laba5.data.entity.FavoriteEntity

@Database(
    entities = [AirportEntity::class, FavoriteEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun airportDao(): AirportDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "flight_search.db"
            )
                .createFromAsset("flight_search.db")
                .build()
        }
    }
}