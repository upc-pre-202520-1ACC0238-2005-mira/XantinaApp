package com.upc.xantina.features.conecta.domain.model

data class Publicacion(
    val id: Int,
    val nombreUsuario: String,
    val tiempo: String,
    val etiqueta: String,
    val descripcionCafe: String,
    val contenidoVisual: String,
    val calificacion: Int,
    val comentarios: Int
)
