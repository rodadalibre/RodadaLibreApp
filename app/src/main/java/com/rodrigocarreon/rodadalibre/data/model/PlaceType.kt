package com.rodrigocarreon.rodadalibre.data.model

import com.google.gson.annotations.SerializedName

enum class PlaceType {
    @SerializedName("station") STATION,
    @SerializedName("store") STORE,
    @SerializedName("workshop") WORKSHOP,
    @SerializedName("restroom") RESTROOM,
    @SerializedName("unknown") UNKNOWN
}