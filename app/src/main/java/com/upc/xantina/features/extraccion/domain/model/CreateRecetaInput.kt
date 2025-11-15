package com.upc.xantina.features.extraccion.domain.model

data class CreateRecetaInput(
    val nombre: String,
    val metodo: String,
    val etiqueta: String? = null,
    val descripcion: String? = null,
    val ratio: String,
    val gramosCafe: Double? = null,
    val mililitrosAgua: Double? = null,
    val temperaturaAgua: Int? = null,
    val tiempoExtraccion: Int? = null, // en segundos
    val calificacion: Int? = null,
    val notas: String? = null
)

