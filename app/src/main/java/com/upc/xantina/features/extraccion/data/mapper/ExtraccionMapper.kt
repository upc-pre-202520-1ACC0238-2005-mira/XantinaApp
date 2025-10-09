package com.upc.xantina.features.extraccion.data.mapper

import com.upc.xantina.features.extraccion.data.datasource.ExtraccionDto
import com.upc.xantina.features.extraccion.data.datasource.CreateExtraccionRequest
import com.upc.xantina.features.extraccion.domain.model.Extraccion
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Mapper para convertir entre DTOs y entidades de Extraccion
 */
object ExtraccionMapper {
    
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    /**
     * Convierte DTO a entidad de dominio
     */
    fun toDomain(dto: ExtraccionDto): Extraccion {
        return Extraccion(
            id = dto.id,
            nombreCafe = dto.nombreCafe,
            metodoExtraccion = dto.metodoExtraccion,
            fechaHora = LocalDateTime.parse(dto.fechaHora, dateTimeFormatter),
            calificacion = dto.calificacion,
            notas = dto.notas,
            gramosCafe = dto.gramosCafe,
            mililitrosAgua = dto.mililitrosAgua,
            temperaturaAgua = dto.temperaturaAgua,
            tiempoExtraccion = dto.tiempoExtraccion,
            usuarioId = dto.usuarioId
        )
    }
    
    /**
     * Convierte entidad de dominio a DTO
     */
    fun toDto(domain: Extraccion): ExtraccionDto {
        return ExtraccionDto(
            id = domain.id,
            nombreCafe = domain.nombreCafe,
            metodoExtraccion = domain.metodoExtraccion,
            fechaHora = domain.fechaHora.format(dateTimeFormatter),
            calificacion = domain.calificacion,
            notas = domain.notas,
            gramosCafe = domain.gramosCafe,
            mililitrosAgua = domain.mililitrosAgua,
            temperaturaAgua = domain.temperaturaAgua,
            tiempoExtraccion = domain.tiempoExtraccion,
            usuarioId = domain.usuarioId
        )
    }
    
    /**
     * Convierte CreateExtraccionRequest a entidad de dominio
     */
    fun fromCreateRequest(request: CreateExtraccionRequest): Extraccion {
        return Extraccion(
            nombreCafe = request.nombreCafe,
            metodoExtraccion = request.metodoExtraccion,
            fechaHora = LocalDateTime.now(),
            calificacion = request.calificacion,
            notas = request.notas,
            gramosCafe = request.gramosCafe,
            mililitrosAgua = request.mililitrosAgua,
            temperaturaAgua = request.temperaturaAgua,
            tiempoExtraccion = request.tiempoExtraccion,
            usuarioId = request.usuarioId
        )
    }
    
    /**
     * Convierte lista de DTOs a lista de entidades
     */
    fun toDomainList(dtoList: List<ExtraccionDto>): List<Extraccion> {
        return dtoList.map { toDomain(it) }
    }
    
    /**
     * Convierte lista de entidades a lista de DTOs
     */
    fun toDtoList(domainList: List<Extraccion>): List<ExtraccionDto> {
        return domainList.map { toDto(it) }
    }
}
