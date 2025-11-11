package com.upc.xantina.features.extraccion.domain.usecase

import com.upc.xantina.features.extraccion.domain.model.Extraccion
import com.upc.xantina.features.extraccion.domain.repository.ExtraccionRepository

class GuardarExtraccionUseCase(
    private val extraccionRepository: ExtraccionRepository
) {
    suspend operator fun invoke(extraccion: Extraccion): Result<Extraccion> {
        return extraccionRepository.guardarExtraccion(extraccion)
    }
}

