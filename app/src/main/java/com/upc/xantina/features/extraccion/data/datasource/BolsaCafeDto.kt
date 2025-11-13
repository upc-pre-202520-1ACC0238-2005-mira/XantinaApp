package com.upc.xantina.features.extraccion.data.datasource

import com.google.gson.annotations.SerializedName

data class BolsaCafeDto(
    val id: String,
    val nombre: String,
    val origen: String?,
    val tostador: String?,
    val varietal: String?,
    val notas: String?,
    @SerializedName("pesoInicial")
    val pesoInicial: Double,
    @SerializedName("pesoRestante")
    val pesoRestante: Double,
    @SerializedName("moliendaSugerida")
    val moliendaSugerida: String?
)


