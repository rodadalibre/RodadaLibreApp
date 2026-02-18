package com.rodrigocarreon.rodadalibre.di

import android.content.Context
import androidx.room.Room
import com.rodrigocarreon.rodadalibre.data.database.RodadaLibreDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val DATABASE_NAME = "rodada_libre_database"

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context) = Room.databaseBuilder(context,
        RodadaLibreDatabase::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun providePlaceDao(db: RodadaLibreDatabase) = db.getPlaceDao()
}