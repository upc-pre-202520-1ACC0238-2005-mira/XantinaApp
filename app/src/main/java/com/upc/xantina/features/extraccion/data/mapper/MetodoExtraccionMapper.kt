package com.upc.xantina.features.extraccion.data.mapper

import com.upc.xantina.features.extraccion.data.datasource.MetodoExtraccionDto
import com.upc.xantina.features.extraccion.data.datasource.ConfiguracionMetodoDto
import com.upc.xantina.features.extraccion.data.datasource.PasoExtraccionDto
import com.upc.xantina.features.extraccion.data.datasource.BaseParametrosDto
import com.upc.xantina.features.extraccion.domain.model.MetodoExtraccion
import com.upc.xantina.features.extraccion.domain.model.Dificultad
import com.upc.xantina.features.extraccion.domain.model.ConfiguracionMetodo
import com.upc.xantina.features.extraccion.domain.model.PasoExtraccion
import com.upc.xantina.features.extraccion.domain.model.BaseParametros

/**
 * Mapper para convertir entre DTOs y entidades de MetodoExtraccion
 */
object MetodoExtraccionMapper {
    
    /**
     * Convierte DTO a entidad de dominio
     */
    fun toDomain(dto: MetodoExtraccionDto): MetodoExtraccion {
        return MetodoExtraccion(
            id = dto.id,
            nombre = dto.nombre,
            descripcion = dto.descripcion,
            tiempoPreparacion = dto.tiempoPreparacion,
            icono = dto.icono,
            dificultad = mapDificultad(dto.dificultad),
            temperatura = dto.temperatura,
            ratio = dto.ratio,
            creadorId = "",
            esPublica = true,
            configuracion = dto.configuracion?.let { mapConfiguracion(it) },
            esPorDefecto = dto.esPorDefecto ?: false
        )
    }
    
    /**
     * Convierte configuración DTO a entidad
     */
    private fun mapConfiguracion(dto: ConfiguracionMetodoDto): ConfiguracionMetodo {
        return ConfiguracionMetodo(
            grind = dto.grind,
            temperature = dto.temperature,
            base = BaseParametros(
                cafeG = dto.base.cafeG,
                aguaTotalMl = dto.base.aguaTotalMl
            ),
            totalTimeSeconds = dto.totalTimeSeconds,
            steps = dto.steps.map { mapPaso(it) }
        )
    }
    
    /**
     * Convierte paso DTO a entidad
     */
    private fun mapPaso(dto: PasoExtraccionDto): PasoExtraccion {
        return PasoExtraccion(
            step = dto.step,
            timeStart = dto.timeStart,
            timeEnd = dto.timeEnd,
            action = dto.action,
            waterMl = dto.waterMl,
            calculation = dto.calculation,
            requiereAccionManual = dto.requiereAccionManual ?: false
        )
    }
    
    /**
     * Convierte entidad de dominio a DTO
     */
    fun toDto(domain: MetodoExtraccion): MetodoExtraccionDto {
        return MetodoExtraccionDto(
            id = domain.id,
            nombre = domain.nombre,
            descripcion = domain.descripcion,
            tiempoPreparacion = domain.tiempoPreparacion,
            icono = domain.icono,
            dificultad = mapDificultadToString(domain.dificultad),
            temperatura = domain.temperatura,
            ratio = domain.ratio,
            configuracion = domain.configuracion?.let { mapConfiguracionToDto(it) },
            esPorDefecto = domain.esPorDefecto
        )
    }
    
    /**
     * Convierte configuración entidad a DTO
     */
    private fun mapConfiguracionToDto(config: ConfiguracionMetodo): ConfiguracionMetodoDto {
        return ConfiguracionMetodoDto(
            grind = config.grind,
            temperature = config.temperature,
            base = BaseParametrosDto(
                cafeG = config.base.cafeG,
                aguaTotalMl = config.base.aguaTotalMl
            ),
            totalTimeSeconds = config.totalTimeSeconds,
            steps = config.steps.map { mapPasoToDto(it) }
        )
    }
    
    /**
     * Convierte paso entidad a DTO
     */
    private fun mapPasoToDto(paso: PasoExtraccion): PasoExtraccionDto {
        return PasoExtraccionDto(
            step = paso.step,
            timeStart = paso.timeStart,
            timeEnd = paso.timeEnd,
            action = paso.action,
            waterMl = paso.waterMl,
            calculation = paso.calculation,
            requiereAccionManual = paso.requiereAccionManual
        )
    }
    
    /**
     * Convierte lista de DTOs a lista de entidades
     */
    fun toDomainList(dtoList: List<MetodoExtraccionDto>): List<MetodoExtraccion> {
        return dtoList.map { toDomain(it) }
    }
    
    /**
     * Convierte lista de entidades a lista de DTOs
     */
    fun toDtoList(domainList: List<MetodoExtraccion>): List<MetodoExtraccionDto> {
        return domainList.map { toDto(it) }
    }
    
    /**
     * Mapea string de dificultad a enum
     */
    private fun mapDificultad(dificultadString: String): Dificultad {
        return when (dificultadString.lowercase()) {
            "facil", "fácil", "easy" -> Dificultad.FACIL
            "intermedio", "intermediate" -> Dificultad.INTERMEDIO
            "avanzado", "advanced", "difícil", "dificil" -> Dificultad.AVANZADO
            else -> Dificultad.INTERMEDIO
        }
    }
    
    /**
     * Mapea enum de dificultad a string
     */
    private fun mapDificultadToString(dificultad: Dificultad): String {
        return when (dificultad) {
            Dificultad.FACIL -> "facil"
            Dificultad.INTERMEDIO -> "intermedio"
            Dificultad.AVANZADO -> "avanzado"
        }
    }
}
