package com.upc.xantina.features.extraccion.data.datasource

/**
 * DTO para extracciones de café provenientes del backend.
 */
data class ExtraccionDto(
    val id: String? = null,
    val nombre: String,
    val metodo: String,
    val ratio: String,
    val notas: String? = null,
    val usuarioId: String,
    val calificacion: Int? = null,
    val gramosCafe: Double? = null,
    val mililitrosAgua: Double? = null,
    val temperaturaAgua: Int? = null,
    val tiempoExtraccion: Int? = null,
    val esPublica: Boolean = true,
    val createdAt: String,
    val updatedAt: String? = null
)

/**
 * DTO para crear una nueva extracción
 */
data class CreateExtraccionRequest(
    val nombre: String,
    val metodo: String,
    val ratio: String,
    val usuarioId: String,
    val notas: String? = null,
    val calificacion: Int? = null,
    val gramosCafe: Double? = null,
    val mililitrosAgua: Double? = null,
    val temperaturaAgua: Int? = null,
    val tiempoExtraccion: Int? = null,
    val esPublica: Boolean = true
)

/**
 * DTO para actualizar una extracción existente
 */
data class UpdateExtraccionRequest(
    val id: String,
    val nombre: String? = null,
    val metodo: String? = null,
    val ratio: String? = null,
    val usuarioId: String? = null,
    val notas: String? = null,
    val calificacion: Int? = null,
    val gramosCafe: Double? = null,
    val mililitrosAgua: Double? = null,
    val temperaturaAgua: Int? = null,
    val tiempoExtraccion: Int? = null,
    val esPublica: Boolean? = null
)
