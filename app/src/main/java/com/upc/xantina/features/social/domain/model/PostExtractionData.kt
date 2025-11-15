package com.upc.xantina.features.social.domain.model

data class PostExtractionData(
    val recetaId: String?,
    val nombre: String?,
    val metodo: String?,
    val ratio: String?,
    val gramosCafe: Double?,
    val mililitrosAgua: Double?,
    val temperaturaAgua: Int?,
    val tiempoExtraccion: Int?,
    val configuracion: ConfiguracionMetodo?,
    val notas: String?
)

data class ConfiguracionMetodo(
    val grind: String?,
    val temperature: String?,
    val base: BaseConfig?,
    val totalTimeSeconds: Int?,
    val steps: List<PasoExtraccion>?
)

data class BaseConfig(
    val cafeG: Double?,
    val aguaTotalMl: Double?
)

data class PasoExtraccion(
    val step: Int,
    val timeStart: Int,
    val timeEnd: Int,
    val action: String,
    val waterMl: Double?,
    val calculation: String?
)



