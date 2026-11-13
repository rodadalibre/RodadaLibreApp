package com.rodrigocarreon.rodadalibre.data.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest (
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("password_confirmation") val password_confirmation: String
)