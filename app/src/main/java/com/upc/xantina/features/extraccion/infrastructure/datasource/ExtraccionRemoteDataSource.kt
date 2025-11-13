package com.upc.xantina.features.extraccion.infrastructure.datasource

import com.upc.xantina.features.extraccion.data.datasource.CreateExtraccionRequest
import com.upc.xantina.features.extraccion.data.datasource.ExtraccionDto
import com.upc.xantina.features.extraccion.data.datasource.BolsaCafeDto
import com.upc.xantina.features.extraccion.data.datasource.ConsumirBolsaCafeRequest
import com.upc.xantina.features.extraccion.data.datasource.CreateBolsaCafeRequest
import com.upc.xantina.features.extraccion.data.datasource.UpdateBolsaCafeRequest
import com.upc.xantina.features.extraccion.infrastructure.api.ExtraccionApiService
import javax.inject.Inject

/**
 * Fuente de datos remota para el contexto de extracción.
 */
class ExtraccionRemoteDataSource @Inject constructor(
    private val apiService: ExtraccionApiService
) {

    suspend fun obtenerExtracciones(
        usuarioId: String? = null,
        limit: Int? = null,
        metodo: String? = null
    ): List<ExtraccionDto> {
        return apiService.getExtracciones(
            usuarioId = usuarioId,
            limit = limit,
            metodo = metodo
        )
    }

    suspend fun crearExtraccion(request: CreateExtraccionRequest): ExtraccionDto {
        return apiService.crearExtraccion(request)
    }

    suspend fun obtenerBolsasCafe(token: String): List<BolsaCafeDto> {
        return apiService.getBolsasCafe("Bearer $token")
    }

    suspend fun crearBolsaCafe(token: String, request: CreateBolsaCafeRequest): BolsaCafeDto {
        return apiService.crearBolsaCafe("Bearer $token", request)
    }

    suspend fun actualizarBolsaCafe(
        token: String,
        id: String,
        request: UpdateBolsaCafeRequest
    ): BolsaCafeDto {
        return apiService.actualizarBolsaCafe("Bearer $token", id, request)
    }

    suspend fun eliminarBolsaCafe(token: String, id: String) {
        apiService.eliminarBolsaCafe("Bearer $token", id)
    }

    suspend fun consumirBolsaCafe(
        token: String,
        request: ConsumirBolsaCafeRequest
    ): BolsaCafeDto {
        return apiService.consumirBolsaCafe("Bearer $token", request)
    }
}

