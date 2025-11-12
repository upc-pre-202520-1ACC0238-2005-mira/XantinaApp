package com.upc.xantina.features.auth.infrastructure.datasource.remote

import com.upc.xantina.features.auth.infrastructure.api.AuthApiService
import com.upc.xantina.features.auth.infrastructure.api.dto.AuthResponseDto
import com.upc.xantina.features.auth.infrastructure.api.dto.LoginRequestDto
import com.upc.xantina.features.auth.infrastructure.api.dto.RegisterRequestDto
import javax.inject.Inject

/**
 * Fuente de datos remota para autenticación.
 * Encapsula las llamadas a la API y centraliza los DTO remotos.
 */
class AuthRemoteDataSource @Inject constructor(
    private val apiService: AuthApiService
) {

    suspend fun login(
        email: String,
        password: String
    ): AuthResponseDto = apiService.login(
        LoginRequestDto(email = email, password = password)
    )

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): AuthResponseDto = apiService.register(
        RegisterRequestDto(name = name, email = email, password = password)
    )
}







