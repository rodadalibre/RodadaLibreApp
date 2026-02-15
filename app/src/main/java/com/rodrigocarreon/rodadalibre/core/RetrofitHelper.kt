package com.rodrigocarreon.rodadalibre.core

import retrofit2.Retrofit
import com.rodrigocarreon.rodadalibre.BuildConfig
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    fun getRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}