package com.upc.xantina.features.extraccion.data.datasource

import java.time.LocalDateTime

/**
 * DTO para extracciones de café
 */
data class ExtraccionDto(
    val id: String? = null,
    val nombreCafe: String,
    val metodoExtraccion: String,
    val fechaHora: String, // ISO string format
    val calificacion: Int,
    val notas: String? = null,
    val gramosCafe: Double? = null,
    val mililitrosAgua: Double? = null,
    val temperaturaAgua: Int? = null,
    val tiempoExtraccion: Int? = null,
    val usuarioId: String
)

/**
 * DTO para crear una nueva extracción
 */
data class CreateExtraccionRequest(
    val nombreCafe: String,
    val metodoExtraccion: String,
    val calificacion: Int,
    val notas: String? = null,
    val gramosCafe: Double? = null,
    val mililitrosAgua: Double? = null,
    val temperaturaAgua: Int? = null,
    val tiempoExtraccion: Int? = null,
    val usuarioId: String
)

/**
 * DTO para actualizar una extracción existente
 */
data class UpdateExtraccionRequest(
    val id: String,
    val nombreCafe: String? = null,
    val metodoExtraccion: String? = null,
    val calificacion: Int? = null,
    val notas: String? = null,
    val gramosCafe: Double? = null,
    val mililitrosAgua: Double? = null,
    val temperaturaAgua: Int? = null,
    val tiempoExtraccion: Int? = null
)
