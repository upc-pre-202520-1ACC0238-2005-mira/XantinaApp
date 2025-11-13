package com.upc.xantina.features.extraccion.data.datasource

import com.google.gson.annotations.SerializedName

/**
 * DTO para un paso de extracción
 */
data class PasoExtraccionDto(
    val step: Int,
    @SerializedName("time_start")
    val timeStart: Int,
    @SerializedName("time_end")
    val timeEnd: Int,
    val action: String,
    @SerializedName("water_ml")
    val waterMl: Int,
    val calculation: String?,
    @SerializedName("requiere_accion_manual")
    val requiereAccionManual: Boolean? = false
)

/**
 * DTO para parámetros base del método
 */
data class BaseParametrosDto(
    @SerializedName("cafe_g")
    val cafeG: Int,
    @SerializedName("agua_total_ml")
    val aguaTotalMl: Int
)

/**
 * DTO para configuración del método
 */
data class ConfiguracionMetodoDto(
    val grind: String,
    val temperature: String,
    val base: BaseParametrosDto,
    @SerializedName("total_time_seconds")
    val totalTimeSeconds: Int,
    val steps: List<PasoExtraccionDto>
)

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
    val ratio: String? = null,
    val configuracion: ConfiguracionMetodoDto? = null,
    @SerializedName("esPorDefecto")
    val esPorDefecto: Boolean? = false
)
