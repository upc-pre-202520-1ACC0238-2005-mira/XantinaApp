package com.upc.xantina.features.extraccion.domain.usecase

import com.upc.xantina.features.extraccion.domain.model.BolsaCafe
import com.upc.xantina.features.extraccion.domain.model.BolsaCafeInput
import com.upc.xantina.features.extraccion.domain.repository.ExtraccionRepository
import javax.inject.Inject

class CreateBolsaCafeUseCase @Inject constructor(
    private val extraccionRepository: ExtraccionRepository
) {
    suspend operator fun invoke(input: BolsaCafeInput): Result<BolsaCafe> {
        return extraccionRepository.crearBolsaCafe(input)
    }
}


