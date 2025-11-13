package com.upc.xantina.features.extraccion.presentation.state

import com.upc.xantina.features.extraccion.domain.model.Extraccion
import com.upc.xantina.features.extraccion.domain.model.MetodoExtraccion
import com.upc.xantina.features.extraccion.domain.model.BolsaCafe

data class ExtraccionUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val metodos: List<MetodoExtraccion> = emptyList(),
    val extraccionesRecientes: List<Extraccion> = emptyList(),
    val selectedFiltro: MetodoFiltro = MetodoFiltro.TODOS,
    val isSaving: Boolean = false,
    val successMessage: String? = null,
    val bolsasCafe: List<BolsaCafe> = emptyList(),
    val isLoadingBolsas: Boolean = false
)

enum class MetodoFiltro {
    TODOS,
    MIS_METODOS
}

