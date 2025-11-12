package com.upc.xantina.features.auth.infrastructure.api.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO general para las respuestas de autenticación que incluyen
 * el token JWT y la información del usuario autenticado.
 */
data class AuthResponseDto(
    @SerializedName("access_token")
    val accessToken: String,
    val user: UserResponseDto
)





