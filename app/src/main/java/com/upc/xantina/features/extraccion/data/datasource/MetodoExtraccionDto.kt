package com.upc.xantina.features.extraccion.data.datasource

/**
 * DTO para métodos de extracción
 */
data class MetodoExtraccionDto(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val tiempoPreparacion: String,
    val icono: String,
    val dificultad: String,
    val temperatura: Int? = null,
    val ratio: String? = null
)
