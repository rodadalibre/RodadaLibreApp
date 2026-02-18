package com.rodrigocarreon.rodadalibre.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rodrigocarreon.rodadalibre.data.database.entities.PlaceEntity

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places_table")
    suspend fun getAllPlaces(): List<PlaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<PlaceEntity>)
}