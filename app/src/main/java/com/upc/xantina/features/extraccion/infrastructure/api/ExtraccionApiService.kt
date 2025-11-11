package com.upc.xantina.features.extraccion.infrastructure.api

import com.upc.xantina.features.extraccion.data.datasource.CreateExtraccionRequest
import com.upc.xantina.features.extraccion.data.datasource.ExtraccionDto
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Servicio de Retrofit para el contexto de extracción.
 */
interface ExtraccionApiService {

    @GET("extraccion")
    suspend fun getExtracciones(
        @Query("usuarioId") usuarioId: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("metodo") metodo: String? = null
    ): List<ExtraccionDto>

    @POST("extraccion")
    suspend fun crearExtraccion(
        @Body request: CreateExtraccionRequest
    ): ExtraccionDto
}

