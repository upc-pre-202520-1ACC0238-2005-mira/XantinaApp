package com.upc.xantina.features.extraccion.data.mapper

import com.upc.xantina.features.extraccion.data.datasource.MetodoExtraccionDto
import com.upc.xantina.features.extraccion.domain.model.MetodoExtraccion
import com.upc.xantina.features.extraccion.domain.model.Dificultad

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
            ratio = dto.ratio
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
            ratio = domain.ratio
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
