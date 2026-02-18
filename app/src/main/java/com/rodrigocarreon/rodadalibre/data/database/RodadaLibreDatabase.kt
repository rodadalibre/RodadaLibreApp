package com.rodrigocarreon.rodadalibre.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rodrigocarreon.rodadalibre.data.database.dao.PlaceDao
import com.rodrigocarreon.rodadalibre.data.database.entities.PlaceEntity

@Database(entities = [PlaceEntity::class], version = 1)
abstract class RodadaLibreDatabase: RoomDatabase() {

    abstract fun getPlaceDao(): PlaceDao
}