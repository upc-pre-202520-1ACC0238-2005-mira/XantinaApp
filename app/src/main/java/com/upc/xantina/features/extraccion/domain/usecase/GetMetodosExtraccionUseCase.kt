package com.upc.xantina.features.extraccion.domain.usecase

import com.upc.xantina.features.extraccion.domain.model.MetodoExtraccion
import com.upc.xantina.features.extraccion.domain.repository.ExtraccionRepository

/**
 * Caso de uso para obtener métodos de extracción
 * Encapsula la lógica de negocio para obtener métodos disponibles
 */
class GetMetodosExtraccionUseCase(
    private val extraccionRepository: ExtraccionRepository
) {
    
    /**
     * Ejecuta el caso de uso para obtener métodos de extracción
     * @return Result<List<MetodoExtraccion>> lista de métodos disponibles
     */
    suspend operator fun invoke(): Result<List<MetodoExtraccion>> {
        return extraccionRepository.getMetodosExtraccion()
    }
    
    /**
     * Obtiene métodos de extracción filtrados por dificultad
     * @param dificultad Dificultad a filtrar
     * @return Result<List<MetodoExtraccion>> métodos filtrados
     */
    suspend fun getMetodosPorDificultad(
        dificultad: com.upc.xantina.features.extraccion.domain.model.Dificultad
    ): Result<List<MetodoExtraccion>> {
        val result = extraccionRepository.getMetodosExtraccion()
        return if (result.isSuccess) {
            val metodosFiltrados = result.getOrNull()?.filter { it.dificultad == dificultad } ?: emptyList()
            Result.success(metodosFiltrados)
        } else {
            result
        }
    }
    
    /**
     * Obtiene métodos de extracción rápidos (menos de 4 minutos)
     * @return Result<List<MetodoExtraccion>> métodos rápidos
     */
    suspend fun getMetodosRapidos(): Result<List<MetodoExtraccion>> {
        val result = extraccionRepository.getMetodosExtraccion()
        return if (result.isSuccess) {
            val metodosRapidos = result.getOrNull()?.filter { it.esRapido() } ?: emptyList()
            Result.success(metodosRapidos)
        } else {
            result
        }
    }
}
