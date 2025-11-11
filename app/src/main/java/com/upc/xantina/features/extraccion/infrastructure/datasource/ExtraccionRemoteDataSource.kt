package com.upc.xantina.features.extraccion.infrastructure.datasource

import com.upc.xantina.features.extraccion.data.datasource.ExtraccionDto
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
}

