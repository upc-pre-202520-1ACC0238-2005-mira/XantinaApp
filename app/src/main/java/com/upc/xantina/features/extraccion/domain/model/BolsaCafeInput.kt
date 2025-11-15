package com.upc.xantina.features.extraccion.domain.model

data class BolsaCafeInput(
    val nombre: String,
    val pesoInicial: Double,
    val pesoRestante: Double? = null,
    val origen: String? = null,
    val tostador: String? = null,
    val varietal: String? = null,
    val notas: String? = null,
    val moliendaSugerida: String? = null
)








