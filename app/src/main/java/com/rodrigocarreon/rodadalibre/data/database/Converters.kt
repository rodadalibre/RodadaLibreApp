package com.rodrigocarreon.rodadalibre.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromStringList(urlList: List<String>?): String{
        return Gson().toJson(urlList)
    }

    @TypeConverter
    fun toStringList(url: String): List<String>{
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(url, listType) ?: emptyList()
    }
}