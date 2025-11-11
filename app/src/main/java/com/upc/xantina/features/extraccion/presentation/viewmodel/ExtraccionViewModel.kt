package com.upc.xantina.features.extraccion.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upc.xantina.features.extraccion.domain.model.Extraccion
import com.upc.xantina.features.extraccion.domain.model.MetodoExtraccion
import com.upc.xantina.features.extraccion.domain.usecase.GetExtraccionesRecientesUseCase
import com.upc.xantina.features.extraccion.domain.usecase.GetMetodosExtraccionUseCase
import com.upc.xantina.features.extraccion.domain.usecase.GuardarExtraccionUseCase
import com.upc.xantina.features.extraccion.presentation.state.ExtraccionUiState
import com.upc.xantina.features.extraccion.presentation.state.MetodoFiltro
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime

@HiltViewModel
class ExtraccionViewModel @Inject constructor(
    private val getMetodosExtraccionUseCase: GetMetodosExtraccionUseCase,
    private val getExtraccionesRecientesUseCase: GetExtraccionesRecientesUseCase,
    private val guardarExtraccionUseCase: GuardarExtraccionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExtraccionUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private var metodosTotales: List<MetodoExtraccion> = emptyList()
    private var ultimoUsuarioId: String? = null

    fun cargarDatos(usuarioId: String?, postAction: (() -> Unit)? = null) {
        ultimoUsuarioId = usuarioId
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
            val metodos = metodosResult.getOrElse { emptyList() }
            metodosTotales = metodos

            _uiState.update {
                val filtroActual = it.selectedFiltro
                it.copy(
                    isLoading = false,
                    errorMessage = error?.message,
                    metodos = aplicarFiltro(metodos, filtroActual, usuarioId),
                    extraccionesRecientes = recientesResult.getOrElse { emptyList() }
                )
            }

            postAction?.invoke()
        }
    }

    fun recargar(usuarioId: String?) {
        cargarDatos(usuarioId)
    }

    fun seleccionarFiltro(filtro: MetodoFiltro) {
        val usuarioId = ultimoUsuarioId
        _uiState.update {
            it.copy(
                selectedFiltro = filtro,
                metodos = aplicarFiltro(metodosTotales, filtro, usuarioId)
            )
        }
    }

    fun crearMetodo(usuarioId: String?, datos: MetodoCreacionDatos) {
        if (usuarioId.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Debes iniciar sesión para crear un método."
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, successMessage = null) }

            val extraccion = Extraccion(
                nombreCafe = datos.nombre,
                metodoExtraccion = datos.metodo.ifBlank { datos.nombre },
                fechaHora = LocalDateTime.now(),
                calificacion = datos.calificacion ?: 0,
                ratio = datos.ratio.takeIf { it.isNotBlank() },
                notas = datos.descripcion,
                gramosCafe = datos.gramosCafe,
                mililitrosAgua = datos.mililitrosAgua,
                temperaturaAgua = datos.temperaturaAgua,
                tiempoExtraccion = datos.tiempoExtraccion,
                usuarioId = usuarioId,
                esPublica = true
            )

            val resultado = guardarExtraccionUseCase(extraccion)

            resultado.onSuccess { _ ->
                cargarDatos(ultimoUsuarioId) {
                    _uiState.update { estado ->
                        estado.copy(
                            isSaving = false,
                            successMessage = "Método creado correctamente.",
                            errorMessage = null
                        )
                    }
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "No se pudo crear el método."
                    )
                }
            }
        }
    }

    fun consumirMensajes() {
        _uiState.update { it.copy(successMessage = null) }
    }

    private fun aplicarFiltro(
        metodos: List<MetodoExtraccion>,
        filtro: MetodoFiltro,
        usuarioId: String?
    ): List<MetodoExtraccion> {
        return when (filtro) {
            MetodoFiltro.TODOS -> metodos
            MetodoFiltro.MIS_METODOS -> {
                if (usuarioId.isNullOrBlank()) {
                    emptyList()
                } else {
                    metodos.filter { it.creadorId == usuarioId }
                }
            }
        }
    }
}

data class MetodoCreacionDatos(
    val nombre: String,
    val metodo: String,
    val ratio: String,
    val descripcion: String?,
    val gramosCafe: Double?,
    val mililitrosAgua: Double?,
    val temperaturaAgua: Int?,
    val tiempoExtraccion: Int?,
    val calificacion: Int? = null
)

