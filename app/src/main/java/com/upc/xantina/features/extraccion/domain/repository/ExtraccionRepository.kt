package com.upc.xantina.features.extraccion.domain.repository

import com.upc.xantina.features.extraccion.domain.model.Extraccion
import com.upc.xantina.features.extraccion.domain.model.MetodoExtraccion
import com.upc.xantina.features.extraccion.domain.model.BolsaCafe
import com.upc.xantina.features.extraccion.domain.model.BolsaCafeInput

/**
 * Interface del repositorio de extracción
 * Define los contratos para las operaciones de extracción de café
 */
interface ExtraccionRepository {
    
    /**
     * Obtiene todos los métodos de extracción disponibles
     * @return List<MetodoExtraccion> lista de métodos disponibles
     */
    suspend fun getMetodosExtraccion(): Result<List<MetodoExtraccion>>
    
    /**
     * Obtiene un método de extracción por ID
     * @param id ID del método
     * @return Result<MetodoExtraccion?> el método encontrado o null
     */
    suspend fun getMetodoExtraccionById(id: String): Result<MetodoExtraccion?>
    
    /**
     * Obtiene una extracción/receta por ID
     * @param id ID de la extracción
     * @return Result<Extraccion?> la extracción encontrada o null
     */
    suspend fun getExtraccionById(id: String): Result<Extraccion?>
    
    /**
     * Obtiene las extracciones recientes del usuario
     * @param usuarioId ID del usuario
     * @param limite Número máximo de extracciones a retornar
     * @return Result<List<Extraccion>> lista de extracciones recientes
     */
    suspend fun getExtraccionesRecientes(
        usuarioId: String,
        limite: Int = 10
    ): Result<List<Extraccion>>
    
    /**
     * Obtiene todas las extracciones del usuario
     * @param usuarioId ID del usuario
     * @param offset Offset para paginación
     * @param limite Límite para paginación
     * @return Result<List<Extraccion>> lista paginada de extracciones
     */
    suspend fun getExtraccionesUsuario(
        usuarioId: String,
        offset: Int = 0,
        limite: Int = 20
    ): Result<List<Extraccion>>
    
    /**
     * Guarda una nueva extracción
     * @param extraccion Datos de la extracción
     * @return Result<Extraccion> la extracción guardada con ID
     */
    suspend fun guardarExtraccion(extraccion: Extraccion): Result<Extraccion>
    
    /**
     * Actualiza una extracción existente
     * @param extraccion Datos de la extracción a actualizar
     * @return Result<Extraccion> la extracción actualizada
     */
    suspend fun actualizarExtraccion(extraccion: Extraccion): Result<Extraccion>
    
    /**
     * Elimina una extracción
     * @param extraccionId ID de la extracción a eliminar
     * @return Result<Unit> resultado de la operación
     */
    suspend fun eliminarExtraccion(extraccionId: String): Result<Unit>
    
    /**
     * Busca extracciones por nombre de café
     * @param usuarioId ID del usuario
     * @param nombreCafe Nombre del café a buscar
     * @return Result<List<Extraccion>> lista de extracciones encontradas
     */
    suspend fun buscarExtraccionesPorCafe(
        usuarioId: String,
        nombreCafe: String
    ): Result<List<Extraccion>>
    
    /**
     * Obtiene estadísticas de extracciones del usuario
     * @param usuarioId ID del usuario
     * @return Result<Map<String, Any>> estadísticas (total, promedio calificación, etc.)
     */
    suspend fun getEstadisticasExtracciones(usuarioId: String): Result<Map<String, Any>>

    /**
     * Obtiene las bolsas de café registradas del usuario autenticado
     */
    suspend fun getBolsasCafe(): Result<List<BolsaCafe>>

    /**
     * Descuenta café consumido de una bolsa
     */
    suspend fun consumirBolsaCafe(bolsaId: String, gramos: Double): Result<BolsaCafe>

    /**
     * Crea una nueva bolsa de café
     */
    suspend fun crearBolsaCafe(input: BolsaCafeInput): Result<BolsaCafe>
}
