package com.upc.xantina.features.extraccion.domain.model

/**
 * Representa un paso individual en el proceso de extracción
 */
data class PasoExtraccion(
    val step: Int,
    val timeStart: Int,
    val timeEnd: Int,
    val action: String,
    val waterMl: Int,
    val calculation: String?,
    val requiereAccionManual: Boolean = false
) {
    /**
     * Obtiene la duración del paso en segundos
     */
    fun getDuracionSegundos(): Int = timeEnd - timeStart
}

/**
 * Configuración detallada del método con pasos
 */
data class ConfiguracionMetodo(
    val grind: String,
    val temperature: String,
    val base: BaseParametros,
    val totalTimeSeconds: Int,
    val steps: List<PasoExtraccion>
)

data class BaseParametros(
    val cafeG: Int,
    val aguaTotalMl: Int
)

/**
 * Entidad de dominio MetodoExtraccion
 * Representa un método de preparación de café
 */
data class MetodoExtraccion(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val tiempoPreparacion: String,
    val icono: String,
    val dificultad: Dificultad = Dificultad.INTERMEDIO,
    val temperatura: Int? = null,
    val ratio: String? = null,
    val creadorId: String,
    val esPublica: Boolean = true,
    val configuracion: ConfiguracionMetodo? = null,
    val esPorDefecto: Boolean = false
) {
    /**
     * Obtiene el tiempo de preparación en minutos
     */
    fun getTiempoEnMinutos(): Int {
        return when {
            tiempoPreparacion.contains("2-3") -> 2
            tiempoPreparacion.contains("4") -> 4
            tiempoPreparacion.contains("5") -> 5
            tiempoPreparacion.contains("6") -> 6
            tiempoPreparacion.contains("7") -> 7
            tiempoPreparacion.contains("8") -> 8
            tiempoPreparacion.contains("9") -> 9
            tiempoPreparacion.contains("10") -> 10
            else -> 3
        }
    }
    
    /**
     * Verifica si el método es rápido (menos de 4 minutos)
     */
    fun esRapido(): Boolean = getTiempoEnMinutos() < 4
    
    /**
     * Obtiene la descripción formateada
     */
    fun getDescripcionFormateada(): String {
        return descripcion.lowercase().replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase() else it.toString() 
        }
    }
    
    /**
     * Verifica si tiene configuración de pasos
     */
    fun tienePasosConfigurados(): Boolean = configuracion != null && configuracion.steps.isNotEmpty()
}

enum class Dificultad {
    FACIL, INTERMEDIO, AVANZADO
}
