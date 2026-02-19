package com.rodrigocarreon.rodadalibre.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rodrigocarreon.rodadalibre.data.database.dao.PlaceDao
import com.rodrigocarreon.rodadalibre.data.database.entities.PlaceEntity

@Database(entities = [PlaceEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class RodadaLibreDatabase: RoomDatabase() {
    abstract fun getPlaceDao(): PlaceDao
}