package com.upc.xantina.features.auth.data.datasource

/**
 * DTO para la petición de registro
 */
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
