package com.upc.xantina.features.extraccion.infrastructure.api

import com.upc.xantina.features.extraccion.data.datasource.CreateExtraccionRequest
import com.upc.xantina.features.extraccion.data.datasource.ExtraccionDto
import com.upc.xantina.features.extraccion.data.datasource.BolsaCafeDto
import com.upc.xantina.features.extraccion.data.datasource.CreateBolsaCafeRequest
import com.upc.xantina.features.extraccion.data.datasource.UpdateBolsaCafeRequest
import com.upc.xantina.features.extraccion.data.datasource.ConsumirBolsaCafeRequest
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Header

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

    @GET("extraccion/{id}")
    suspend fun getExtraccionById(
        @Path("id") id: String
    ): ExtraccionDto

    @POST("extraccion")
    suspend fun crearExtraccion(
        @Body request: CreateExtraccionRequest
    ): ExtraccionDto

    @GET("extraccion/cafes")
    suspend fun getBolsasCafe(
        @Header("Authorization") token: String
    ): List<BolsaCafeDto>

    @POST("extraccion/cafes")
    suspend fun crearBolsaCafe(
        @Header("Authorization") token: String,
        @Body request: CreateBolsaCafeRequest
    ): BolsaCafeDto

    @PUT("extraccion/cafes/{id}")
    suspend fun actualizarBolsaCafe(
        @Header("Authorization") token: String,
        @Path("id") bolsaId: String,
        @Body request: UpdateBolsaCafeRequest
    ): BolsaCafeDto

    @DELETE("extraccion/cafes/{id}")
    suspend fun eliminarBolsaCafe(
        @Header("Authorization") token: String,
        @Path("id") bolsaId: String
    )

    @POST("extraccion/cafes/consumir")
    suspend fun consumirBolsaCafe(
        @Header("Authorization") token: String,
        @Body request: ConsumirBolsaCafeRequest
    ): BolsaCafeDto
}

