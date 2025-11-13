package com.upc.xantina.features.extraccion.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upc.xantina.features.extraccion.domain.model.Extraccion
import com.upc.xantina.features.extraccion.domain.model.MetodoExtraccion
import com.upc.xantina.features.extraccion.domain.usecase.GetExtraccionesRecientesUseCase
import com.upc.xantina.features.extraccion.domain.usecase.GetMetodosExtraccionUseCase
import com.upc.xantina.features.extraccion.domain.usecase.GetBolsasCafeUseCase
import com.upc.xantina.features.extraccion.domain.usecase.ConsumirBolsaCafeUseCase
import com.upc.xantina.features.extraccion.domain.usecase.CreateBolsaCafeUseCase
import com.upc.xantina.features.extraccion.domain.usecase.GuardarExtraccionUseCase
import com.upc.xantina.features.extraccion.domain.model.BolsaCafeInput
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
    private val guardarExtraccionUseCase: GuardarExtraccionUseCase,
    private val getBolsasCafeUseCase: GetBolsasCafeUseCase,
    private val consumirBolsaCafeUseCase: ConsumirBolsaCafeUseCase,
    private val createBolsaCafeUseCase: CreateBolsaCafeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExtraccionUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private var metodosTotales: List<MetodoExtraccion> = emptyList()
    private var ultimoUsuarioId: String? = null

    fun cargarDatos(usuarioId: String?, postAction: (() -> Unit)? = null) {
        ultimoUsuarioId = usuarioId
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isLoadingBolsas = true,
                    errorMessage = null
                )
            }

            val metodosDeferred = async { getMetodosExtraccionUseCase() }
            val recientesDeferred = async {
                if (usuarioId.isNullOrBlank()) {
                    Result.success(emptyList())
                } else {
                    getExtraccionesRecientesUseCase(usuarioId, limite = 5)
                }
            }
            val bolsasDeferred = async {
                if (usuarioId.isNullOrBlank()) {
                    Result.success(emptyList())
                } else {
                    getBolsasCafeUseCase()
                }
            }

            val metodosResult = metodosDeferred.await()
            val recientesResult = recientesDeferred.await()
            val bolsasResult = bolsasDeferred.await()

            val error = metodosResult.exceptionOrNull()
                ?: recientesResult.exceptionOrNull()
                ?: bolsasResult.exceptionOrNull()
            val metodos = metodosResult.getOrElse { emptyList() }
            metodosTotales = metodos

            _uiState.update {
                val filtroActual = it.selectedFiltro
                it.copy(
                    isLoading = false,
                    isLoadingBolsas = false,
                    errorMessage = error?.message,
                    metodos = aplicarFiltro(metodos, filtroActual, usuarioId),
                    extraccionesRecientes = recientesResult.getOrElse { emptyList() },
                    bolsasCafe = bolsasResult.getOrElse { emptyList() }
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

    fun refrescarBolsas() {
        val usuarioId = ultimoUsuarioId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBolsas = true) }
            val resultado = getBolsasCafeUseCase()
            _uiState.update { estado ->
                estado.copy(
                    isLoadingBolsas = false,
                    bolsasCafe = resultado.getOrElse { estado.bolsasCafe },
                    errorMessage = resultado.exceptionOrNull()?.message ?: estado.errorMessage
                )
            }
        }
    }

    fun consumirBolsaCafe(bolsaId: String, gramos: Double) {
        if (gramos <= 0) return
        viewModelScope.launch {
            val resultado = consumirBolsaCafeUseCase(bolsaId, gramos)
            resultado.onSuccess { bolsaActualizada ->
                _uiState.update { estado ->
                    estado.copy(
                        bolsasCafe = estado.bolsasCafe.map { bolsa ->
                            if (bolsa.id == bolsaActualizada.id) {
                                bolsa.copy(pesoRestante = bolsaActualizada.pesoRestante)
                            } else bolsa
                        }
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(errorMessage = throwable.message ?: "No se pudo actualizar la bolsa de café.")
                }
            }
        }
    }

    fun crearBolsaCafe(input: BolsaCafeInput) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBolsas = true, errorMessage = null) }
            val resultado = createBolsaCafeUseCase(input)
            resultado.onSuccess { nuevaBolsa ->
                _uiState.update { estado ->
                    estado.copy(
                        isLoadingBolsas = false,
                        bolsasCafe = listOf(nuevaBolsa) + estado.bolsasCafe,
                        successMessage = "Bolsa ${nuevaBolsa.nombre} registrada."
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoadingBolsas = false,
                        errorMessage = throwable.message ?: "No se pudo registrar la bolsa de café."
                    )
                }
            }
        }
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

