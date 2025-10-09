package com.upc.xantina.features.auth.data.datasource

/**
 * DTO para la petición de login
 */
data class LoginRequest(
    val email: String,
    val password: String
)
