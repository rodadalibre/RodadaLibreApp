package com.rodrigocarreon.rodadalibre.data.model

import com.google.gson.annotations.SerializedName

data class DeleteAccountRequest(
    @SerializedName("current_password") val currentPassword: String
)
