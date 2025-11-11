package com.upc.xantina.features.extraccion.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upc.xantina.features.extraccion.domain.usecase.GetExtraccionesRecientesUseCase
import com.upc.xantina.features.extraccion.domain.usecase.GetMetodosExtraccionUseCase
import com.upc.xantina.features.extraccion.presentation.state.ExtraccionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class ExtraccionViewModel @Inject constructor(
    private val getMetodosExtraccionUseCase: GetMetodosExtraccionUseCase,
    private val getExtraccionesRecientesUseCase: GetExtraccionesRecientesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExtraccionUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    fun cargarDatos(usuarioId: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val metodosDeferred = async { getMetodosExtraccionUseCase() }
            val recientesDeferred = async {
                if (usuarioId.isNullOrBlank()) {
                    Result.success(emptyList())
                } else {
                    getExtraccionesRecientesUseCase(usuarioId, limite = 5)
                }
            }

            val metodosResult = metodosDeferred.await()
            val recientesResult = recientesDeferred.await()

            val error = metodosResult.exceptionOrNull() ?: recientesResult.exceptionOrNull()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = error?.message,
                    metodos = metodosResult.getOrElse { emptyList() },
                    extraccionesRecientes = recientesResult.getOrElse { emptyList() }
                )
            }
        }
    }

    fun recargar(usuarioId: String?) {
        cargarDatos(usuarioId)
    }
}

