package com.upc.xantina.features.social.infrastructure.api.dto

import com.google.gson.annotations.SerializedName

data class PostExtractionDataDto(
    @SerializedName("recetaId")
    val recetaId: String?,
    val nombre: String?,
    val metodo: String?,
    val ratio: String?,
    @SerializedName("gramosCafe")
    val gramosCafe: Double?,
    @SerializedName("mililitrosAgua")
    val mililitrosAgua: Double?,
    @SerializedName("temperaturaAgua")
    val temperaturaAgua: Int?,
    @SerializedName("tiempoExtraccion")
    val tiempoExtraccion: Int?,
    val configuracion: ConfiguracionMetodoDto?,
    val notas: String?
)

data class ConfiguracionMetodoDto(
    val grind: String?,
    val temperature: String?,
    val base: BaseConfigDto?,
    @SerializedName("total_time_seconds")
    val totalTimeSeconds: Int?,
    val steps: List<PasoExtraccionDto>?
)

data class BaseConfigDto(
    @SerializedName("cafe_g")
    val cafeG: Double?,
    @SerializedName("agua_total_ml")
    val aguaTotalMl: Double?
)

data class PasoExtraccionDto(
    val step: Int,
    @SerializedName("time_start")
    val timeStart: Int,
    @SerializedName("time_end")
    val timeEnd: Int,
    val action: String,
    @SerializedName("water_ml")
    val waterMl: Double?,
    val calculation: String?
)

