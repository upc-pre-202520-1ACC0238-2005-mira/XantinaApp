package com.upc.xantina.features.extraccion.data.datasource

data class CreateBolsaCafeRequest(
    val nombre: String,
    val pesoInicial: Double,
    val pesoRestante: Double? = null,
    val origen: String? = null,
    val tostador: String? = null,
    val varietal: String? = null,
    val notas: String? = null,
    val moliendaSugerida: String? = null
)

data class UpdateBolsaCafeRequest(
    val nombre: String? = null,
    val pesoInicial: Double? = null,
    val pesoRestante: Double? = null,
    val origen: String? = null,
    val tostador: String? = null,
    val varietal: String? = null,
    val notas: String? = null,
    val moliendaSugerida: String? = null
)

data class ConsumirBolsaCafeRequest(
    val bolsaId: String,
    val gramos: Double
)


