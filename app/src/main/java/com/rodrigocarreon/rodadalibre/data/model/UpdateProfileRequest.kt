package com.rodrigocarreon.rodadalibre.data.model

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String
)
