package com.upc.xantina.features.extraccion.ui

import com.upc.xantina.features.extraccion.domain.model.PasoExtraccion

/**
 * Convierte un PasoExtraccion del dominio a PasoExtraccionUi para mostrar en pantalla
 */
fun PasoExtraccion.toUi(): PasoExtraccionUi {
    return PasoExtraccionUi(
        titulo = "Paso $step",
        instruccion = this.action,
        duracionSegundos = this.getDuracionSegundos(),
        animacion = obtenerAnimacionPorAccion(this.action),
        recordatorio = obtenerRecordatorio(this.action, this.waterMl),
        requiereAccionManual = this.requiereAccionManual
    )
}

/**
 * Convierte una lista de pasos del dominio a lista UI
 */
fun List<PasoExtraccion>.toUiList(): List<PasoExtraccionUi> {
    return this.map { it.toUi() }
}

/**
 * Obtiene la animación apropiada según la acción del paso
 */
private fun obtenerAnimacionPorAccion(accion: String): PasoAnimacion {
    val accionLower = accion.lowercase()
    
    return when {
        accionLower.contains("viert") || accionLower.contains("bloom") -> {
            PasoAnimacion.Lottie(
                url = "https://lottie.host/090315c6-44d9-45df-b120-11b6760dd72e/8RD85LN6uY.lottie",
                speed = 3f
            )
        }
        accionLower.contains("remueve") || accionLower.contains("mezcla") -> {
            PasoAnimacion.Lottie(
                url = "https://lottie.host/090315c6-44d9-45df-b120-11b6760dd72e/8RD85LN6uY.lottie",
                speed = 2.5f
            )
        }
        accionLower.contains("espera") || accionLower.contains("reposo") || 
        accionLower.contains("escurrir") || accionLower.contains("deja") -> {
            PasoAnimacion.Lottie(
                url = "https://lottie.host/4e7cce53-b9cd-468f-accf-0b9511c28d65/i8hf0Nuvna.lottie",
                speed = 2.4f
            )
        }
        accionLower.contains("presiona") || accionLower.contains("gira") -> {
            PasoAnimacion.Lottie(
                url = "https://lottie.host/090315c6-44d9-45df-b120-11b6760dd72e/8RD85LN6uY.lottie",
                speed = 1.5f
            )
        }
        else -> {
            // Animación por defecto
            PasoAnimacion.Lottie(
                url = "https://lottie.host/4e7cce53-b9cd-468f-accf-0b9511c28d65/i8hf0Nuvna.lottie",
                speed = 2f
            )
        }
    }
}

/**
 * Genera un recordatorio contextual basado en la acción y cantidad de agua
 */
private fun obtenerRecordatorio(accion: String, aguaMl: Int): String {
    val accionLower = accion.lowercase()
    
    return when {
        accionLower.contains("bloom") && aguaMl > 0 -> {
            "Asegúrate de mojar todo el café uniformemente con los $aguaMl ml"
        }
        accionLower.contains("viert") && aguaMl > 0 -> {
            "Vierte los $aguaMl ml en movimientos circulares constantes"
        }
        accionLower.contains("remueve") -> {
            "Haz movimientos suaves para no romper el filtro"
        }
        accionLower.contains("espera") || accionLower.contains("reposo") -> {
            "Aprovecha este tiempo para preparar tu taza favorita"
        }
        accionLower.contains("presiona") -> {
            "Presiona de forma constante, sin apurar el proceso"
        }
        accionLower.contains("enjuaga") -> {
            "Esto elimina sabores del papel y precalienta el recipiente"
        }
        else -> {
            "Sigue las instrucciones cuidadosamente para mejores resultados"
        }
    }
}

