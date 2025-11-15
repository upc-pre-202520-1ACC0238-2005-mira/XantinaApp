package com.upc.xantina.features.extraccion.domain.model

data class BolsaCafe(
    val id: String,
    val nombre: String,
    val origen: String?,
    val tostador: String?,
    val varietal: String?,
    val notas: String?,
    val pesoInicial: Double,
    val pesoRestante: Double,
    val moliendaSugerida: String?
)








