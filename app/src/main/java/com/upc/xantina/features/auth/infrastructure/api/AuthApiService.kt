package com.upc.xantina.features.auth.infrastructure.api

import com.upc.xantina.features.auth.infrastructure.api.dto.AuthResponseDto
import com.upc.xantina.features.auth.infrastructure.api.dto.LoginRequestDto
import com.upc.xantina.features.auth.infrastructure.api.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Servicio de Retrofit para el módulo de autenticación.
 * Define los endpoints expuestos por el backend NestJS.
 */
interface AuthApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): AuthResponseDto

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): AuthResponseDto
}







