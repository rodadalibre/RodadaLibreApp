package com.rodrigocarreon.rodadalibre.di

import androidx.compose.ui.tooling.preview.Preview
import com.rodrigocarreon.rodadalibre.BuildConfig
import com.rodrigocarreon.rodadalibre.data.network.AuthApiClient
import com.rodrigocarreon.rodadalibre.data.network.PlaceClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun providePlaceClient(retrofit: Retrofit): PlaceClient{
        return retrofit.create(PlaceClient::class.java)
    }

    @Singleton
    @Provides
    fun provideAuthApiClient(retrofit: Retrofit): AuthApiClient{
        return retrofit.create(AuthApiClient::class.java)
    }
}