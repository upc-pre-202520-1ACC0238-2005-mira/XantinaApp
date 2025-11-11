package com.upc.xantina.features.extraccion.domain.model

import java.time.LocalDateTime

/**
 * Entidad de dominio Extraccion
 * Representa una extracción de café realizada por el usuario
 */
data class Extraccion(
    val id: String? = null,
    val nombreCafe: String,
    val metodoExtraccion: String,
    val fechaHora: LocalDateTime,
    val calificacion: Int = 0,
    val ratio: String? = null,
    val notas: String? = null,
    val gramosCafe: Double? = null,
    val mililitrosAgua: Double? = null,
    val temperaturaAgua: Int? = null,
    val tiempoExtraccion: Int? = null,
    val usuarioId: String
) {
    /**
     * Valida que la calificación esté en el rango correcto (1-5)
     */
    fun esCalificacionValida(): Boolean {
        return calificacion in 1..5
    }
    
    /**
     * Obtiene la fecha formateada para mostrar
     */
    fun getFechaFormateada(): String {
        return when {
            fechaHora.toLocalDate().isEqual(java.time.LocalDate.now()) -> "Hoy"
            fechaHora.toLocalDate().isEqual(java.time.LocalDate.now().minusDays(1)) -> "Ayer"
            else -> fechaHora.toLocalDate().toString()
        }
    }
    
    /**
     * Obtiene la hora formateada
     */
    fun getHoraFormateada(): String {
        return fechaHora.toLocalTime().format(
            java.time.format.DateTimeFormatter.ofPattern("h:mm a")
        )
    }
    
    /**
     * Obtiene la fecha y hora completa formateada
     */
    fun getFechaHoraCompleta(): String {
        return "${getFechaFormateada()}, ${getHoraFormateada()}"
    }
    
    /**
     * Obtiene las estrellas de calificación como lista de booleanos
     */
    fun getEstrellasCalificacion(): List<Boolean> {
        return (1..5).map { it <= calificacion }
    }
    
    /**
     * Calcula el ratio café/agua si ambos valores están disponibles
     */
    fun calcularRatio(): String? {
        if (!ratio.isNullOrBlank()) {
            return ratio
        }
        return if (gramosCafe != null && mililitrosAgua != null && mililitrosAgua > 0) {
            val proporcion = mililitrosAgua / gramosCafe
            "1:${String.format("%.0f", proporcion)}"
        } else null
    }
}
