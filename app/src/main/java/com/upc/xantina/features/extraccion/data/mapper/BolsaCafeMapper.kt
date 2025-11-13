package com.upc.xantina.features.extraccion.data.mapper

import com.upc.xantina.features.extraccion.data.datasource.BolsaCafeDto
import com.upc.xantina.features.extraccion.domain.model.BolsaCafe

object BolsaCafeMapper {

    fun toDomain(dto: BolsaCafeDto): BolsaCafe {
        return BolsaCafe(
            id = dto.id,
            nombre = dto.nombre,
            origen = dto.origen,
            tostador = dto.tostador,
            varietal = dto.varietal,
            notas = dto.notas,
            pesoInicial = dto.pesoInicial,
            pesoRestante = dto.pesoRestante,
            moliendaSugerida = dto.moliendaSugerida
        )
    }

    fun toDomainList(dtos: List<BolsaCafeDto>): List<BolsaCafe> =
        dtos.map { toDomain(it) }
}


