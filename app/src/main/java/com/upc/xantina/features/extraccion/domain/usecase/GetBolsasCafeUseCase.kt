package com.upc.xantina.features.extraccion.domain.usecase

import com.upc.xantina.features.extraccion.domain.model.BolsaCafe
import com.upc.xantina.features.extraccion.domain.repository.ExtraccionRepository
import javax.inject.Inject

class GetBolsasCafeUseCase @Inject constructor(
    private val extraccionRepository: ExtraccionRepository
) {
    suspend operator fun invoke(): Result<List<BolsaCafe>> {
        return extraccionRepository.getBolsasCafe()
    }
}








