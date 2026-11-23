package com.rodrigocarreon.rodadalibre.data.model

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("password") val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String
)

enum class ChangePasswordResult { SUCCESS, WRONG_CURRENT_PASSWORD, ERROR }
