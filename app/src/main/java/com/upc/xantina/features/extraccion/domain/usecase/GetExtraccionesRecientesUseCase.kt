package com.upc.xantina.features.extraccion.domain.usecase

import com.upc.xantina.features.extraccion.domain.model.Extraccion
import com.upc.xantina.features.extraccion.domain.repository.ExtraccionRepository

/**
 * Caso de uso para obtener extracciones recientes
 * Encapsula la lógica de negocio para obtener extracciones recientes del usuario
 */
class GetExtraccionesRecientesUseCase(
    private val extraccionRepository: ExtraccionRepository
) {
    
    /**
     * Ejecuta el caso de uso para obtener extracciones recientes
     * @param usuarioId ID del usuario
     * @param limite Número máximo de extracciones a retornar (default: 5)
     * @return Result<List<Extraccion>> lista de extracciones recientes
     */
    suspend operator fun invoke(
        usuarioId: String,
        limite: Int = 5
    ): Result<List<Extraccion>> {
        
        // Validaciones de negocio
        if (usuarioId.isBlank()) {
            return Result.failure(Exception("El ID del usuario es requerido"))
        }
        
        if (limite <= 0) {
            return Result.failure(Exception("El límite debe ser mayor a 0"))
        }
        
        if (limite > 20) {
            return Result.failure(Exception("El límite no puede ser mayor a 20"))
        }
        
        return extraccionRepository.getExtraccionesRecientes(usuarioId, limite)
    }
    
    /**
     * Obtiene extracciones recientes con calificación mínima
     * @param usuarioId ID del usuario
     * @param calificacionMinima Calificación mínima (1-5)
     * @param limite Número máximo de extracciones
     * @return Result<List<Extraccion>> extracciones filtradas por calificación
     */
    suspend fun getExtraccionesConCalificacionMinima(
        usuarioId: String,
        calificacionMinima: Int,
        limite: Int = 5
    ): Result<List<Extraccion>> {
        
        if (calificacionMinima !in 1..5) {
            return Result.failure(Exception("La calificación debe estar entre 1 y 5"))
        }
        
        val result = invoke(usuarioId, limite)
        return if (result.isSuccess) {
            val extraccionesFiltradas = result.getOrNull()?.filter { 
                it.calificacion >= calificacionMinima 
            } ?: emptyList()
            Result.success(extraccionesFiltradas)
        } else {
            result
        }
    }
    
    /**
     * Obtiene extracciones recientes por método específico
     * @param usuarioId ID del usuario
     * @param metodoExtraccion Método de extracción a filtrar
     * @param limite Número máximo de extracciones
     * @return Result<List<Extraccion>> extracciones filtradas por método
     */
    suspend fun getExtraccionesPorMetodo(
        usuarioId: String,
        metodoExtraccion: String,
        limite: Int = 5
    ): Result<List<Extraccion>> {
        
        if (metodoExtraccion.isBlank()) {
            return Result.failure(Exception("El método de extracción es requerido"))
        }
        
        val result = invoke(usuarioId, limite * 2) // Buscamos más para filtrar
        return if (result.isSuccess) {
            val extraccionesFiltradas = result.getOrNull()?.filter { 
                it.metodoExtraccion.equals(metodoExtraccion, ignoreCase = true) 
            }?.take(limite) ?: emptyList()
            Result.success(extraccionesFiltradas)
        } else {
            result
        }
    }
}
