package com.rodrigocarreon.rodadalibre.data.model

import com.google.gson.annotations.SerializedName

data class PlaceListResponse (
    @SerializedName("data")
    val data: List<PlaceModel>
)