package com.upc.xantina.features.extraccion.domain.usecase

import com.upc.xantina.features.extraccion.domain.model.BolsaCafe
import com.upc.xantina.features.extraccion.domain.repository.ExtraccionRepository
import javax.inject.Inject

class ConsumirBolsaCafeUseCase @Inject constructor(
    private val extraccionRepository: ExtraccionRepository
) {

    suspend operator fun invoke(bolsaId: String, gramos: Double): Result<BolsaCafe> {
        return extraccionRepository.consumirBolsaCafe(bolsaId, gramos)
    }
}


