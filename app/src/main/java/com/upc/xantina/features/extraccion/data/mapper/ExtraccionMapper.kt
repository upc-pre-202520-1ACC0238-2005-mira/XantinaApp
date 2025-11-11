package com.upc.xantina.features.extraccion.data.mapper

import com.upc.xantina.features.extraccion.data.datasource.CreateExtraccionRequest
import com.upc.xantina.features.extraccion.data.datasource.ExtraccionDto
import com.upc.xantina.features.extraccion.domain.model.Extraccion
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Mapper para convertir entre DTOs y entidades de Extraccion
 */
object ExtraccionMapper {

    private val isoLocalFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    private fun parseDateTime(value: String): LocalDateTime {
        return try {
            OffsetDateTime.parse(value).toLocalDateTime()
        } catch (_: DateTimeParseException) {
            LocalDateTime.parse(value, isoLocalFormatter)
        }
    }

    /**
     * Convierte DTO a entidad de dominio
     */
    fun toDomain(dto: ExtraccionDto): Extraccion {
        return Extraccion(
            id = dto.id,
            nombreCafe = dto.nombre,
            metodoExtraccion = dto.metodo,
            fechaHora = parseDateTime(dto.createdAt),
            calificacion = dto.calificacion ?: 0,
            ratio = dto.ratio,
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
            nombre = domain.nombreCafe,
            metodo = domain.metodoExtraccion,
            ratio = domain.calcularRatio() ?: domain.ratio.orEmpty(),
            notas = domain.notas,
            usuarioId = domain.usuarioId,
            calificacion = domain.calificacion,
            gramosCafe = domain.gramosCafe,
            mililitrosAgua = domain.mililitrosAgua,
            temperaturaAgua = domain.temperaturaAgua,
            tiempoExtraccion = domain.tiempoExtraccion,
            createdAt = domain.fechaHora.format(isoLocalFormatter),
            updatedAt = null
        )
    }

    /**
     * Convierte CreateExtraccionRequest a entidad de dominio
     */
    fun fromCreateRequest(request: CreateExtraccionRequest): Extraccion {
        return Extraccion(
            nombreCafe = request.nombre,
            metodoExtraccion = request.metodo,
            fechaHora = LocalDateTime.now(),
            calificacion = request.calificacion ?: 0,
            ratio = request.ratio,
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
