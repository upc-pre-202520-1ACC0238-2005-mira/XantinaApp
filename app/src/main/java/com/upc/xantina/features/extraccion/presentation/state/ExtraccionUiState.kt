package com.upc.xantina.features.extraccion.presentation.state

import com.upc.xantina.features.extraccion.domain.model.Extraccion
import com.upc.xantina.features.extraccion.domain.model.MetodoExtraccion

data class ExtraccionUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val metodos: List<MetodoExtraccion> = emptyList(),
    val extraccionesRecientes: List<Extraccion> = emptyList()
)

