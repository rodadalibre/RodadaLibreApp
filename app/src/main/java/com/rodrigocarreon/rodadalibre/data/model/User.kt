package com.rodrigocarreon.rodadalibre.data.model

import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("id") val id: Int,
    @SerializedName("first_name") val first_name: String,
    @SerializedName("last_name") val last_name: String,
    @SerializedName("email") val email: String
)