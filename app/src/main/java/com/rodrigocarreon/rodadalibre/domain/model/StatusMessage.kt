package com.rodrigocarreon.rodadalibre.domain.model

/** A message for the user: a short bold title, an optional smaller description, and its kind (picks the icon). */
data class StatusMessage(
    val title: String,
    val description: String? = null,
    val isOffline: Boolean = false
)
