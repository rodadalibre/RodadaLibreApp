package com.rodrigocarreon.rodadalibre.data.model

// Result of an action the backend guards with the current password
enum class PasswordActionResult { SUCCESS, WRONG_CURRENT_PASSWORD, ERROR }
