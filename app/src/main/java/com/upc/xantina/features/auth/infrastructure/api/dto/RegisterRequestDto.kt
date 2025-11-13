package com.upc.xantina.features.auth.infrastructure.api.dto

/**
 * DTO para la petición de registro consumida por Retrofit.
 */
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String
)








