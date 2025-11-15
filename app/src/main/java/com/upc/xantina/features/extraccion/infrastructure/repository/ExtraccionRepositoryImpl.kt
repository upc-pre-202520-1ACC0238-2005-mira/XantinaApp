package com.upc.xantina.features.extraccion.infrastructure.repository

import com.upc.xantina.core.domain.repository.AuthRepository
import com.upc.xantina.features.extraccion.data.mapper.ExtraccionMapper
import com.upc.xantina.features.extraccion.data.mapper.BolsaCafeMapper
import com.upc.xantina.features.extraccion.data.datasource.ConsumirBolsaCafeRequest
import com.upc.xantina.features.extraccion.data.datasource.CreateBolsaCafeRequest
import com.upc.xantina.features.extraccion.domain.model.Dificultad
import com.upc.xantina.features.extraccion.domain.model.Extraccion
import com.upc.xantina.features.extraccion.domain.model.MetodoExtraccion
import com.upc.xantina.features.extraccion.domain.model.BolsaCafe
import com.upc.xantina.features.extraccion.domain.model.BolsaCafeInput
import com.upc.xantina.features.extraccion.domain.repository.ExtraccionRepository
import com.upc.xantina.features.extraccion.infrastructure.datasource.ExtraccionRemoteDataSource
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

@Singleton
class ExtraccionRepositoryImpl @Inject constructor(
    private val remoteDataSource: ExtraccionRemoteDataSource,
    private val authRepository: AuthRepository
) : ExtraccionRepository {

    override suspend fun getMetodosExtraccion(): Result<List<MetodoExtraccion>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val extraccionesDto = remoteDataSource.obtenerExtracciones()
                val extracciones = ExtraccionMapper.toDomainList(extraccionesDto)
                extracciones
                    .map { toMetodoExtraccion(it) }
                    .sortedBy { it.nombre }
            }.mapError()
        }

    override suspend fun getMetodoExtraccionById(id: String): Result<MetodoExtraccion?> =
        withContext(Dispatchers.IO) {
            runCatching {
                val extraccionesDto = remoteDataSource.obtenerExtracciones()
                val extracciones = ExtraccionMapper.toDomainList(extraccionesDto)
                extracciones
                    .firstOrNull { it.id == id }
                    ?.let { toMetodoExtraccion(it) }
            }.mapError()
        }

    override suspend fun getExtraccionById(id: String): Result<Extraccion?> =
        withContext(Dispatchers.IO) {
            runCatching {
                val extraccionDto = remoteDataSource.obtenerExtraccionPorId(id)
                ExtraccionMapper.toDomain(extraccionDto)
            }.mapError()
        }

    override suspend fun getExtraccionesRecientes(
        usuarioId: String,
        limite: Int
    ): Result<List<Extraccion>> = withContext(Dispatchers.IO) {
        runCatching {
            val extraccionesDto = remoteDataSource.obtenerExtracciones(
                usuarioId = usuarioId,
                limit = limite
            )
            ExtraccionMapper.toDomainList(extraccionesDto)
        }.mapError()
    }

    override suspend fun getExtraccionesUsuario(
        usuarioId: String,
        offset: Int,
        limite: Int
    ): Result<List<Extraccion>> = withContext(Dispatchers.IO) {
        runCatching {
            // TODO: backend aún no soporta paginación, se ignora offset
            val extraccionesDto = remoteDataSource.obtenerExtracciones(
                usuarioId = usuarioId,
                limit = limite
            )
            ExtraccionMapper.toDomainList(extraccionesDto)
        }.mapError()
    }

    override suspend fun guardarExtraccion(extraccion: Extraccion): Result<Extraccion> =
        withContext(Dispatchers.IO) {
            runCatching {
                val request = ExtraccionMapper.toCreateRequest(extraccion)
                val dto = remoteDataSource.crearExtraccion(request)
                ExtraccionMapper.toDomain(dto)
            }.mapError()
        }

    override suspend fun actualizarExtraccion(extraccion: Extraccion): Result<Extraccion> {
        return Result.failure(UnsupportedOperationException("No implementado aún"))
    }

    private fun toMetodoExtraccion(extraccion: Extraccion): MetodoExtraccion {
        return MetodoExtraccion(
            id = extraccion.id ?: "${extraccion.metodoExtraccion}-${extraccion.usuarioId}",
            nombre = extraccion.metodoExtraccion,
            descripcion = extraccion.notas ?: "Receta: ${extraccion.nombreCafe}",
            tiempoPreparacion = extraccion.tiempoExtraccion?.let { "$it s" } ?: "—",
            icono = extraccion.metodoExtraccion,
            dificultad = inferirDificultad(extraccion),
            temperatura = extraccion.temperaturaAgua,
            ratio = extraccion.calcularRatio(),
            creadorId = extraccion.usuarioId,
            esPublica = extraccion.esPublica
        )
    }

    override suspend fun eliminarExtraccion(extraccionId: String): Result<Unit> {
        return Result.failure(UnsupportedOperationException("No implementado aún"))
    }

    override suspend fun buscarExtraccionesPorCafe(
        usuarioId: String,
        nombreCafe: String
    ): Result<List<Extraccion>> = withContext(Dispatchers.IO) {
        runCatching {
            val extraccionesDto = remoteDataSource.obtenerExtracciones(usuarioId = usuarioId)
            ExtraccionMapper
                .toDomainList(extraccionesDto)
                .filter { it.nombreCafe.contains(nombreCafe, ignoreCase = true) }
        }.mapError()
    }

    override suspend fun getEstadisticasExtracciones(usuarioId: String): Result<Map<String, Any>> {
        return Result.failure(UnsupportedOperationException("No implementado aún"))
    }

    override suspend fun getBolsasCafe(): Result<List<BolsaCafe>> =
        withAuthContext { token ->
            val bolsasDto = remoteDataSource.obtenerBolsasCafe(token)
            BolsaCafeMapper.toDomainList(bolsasDto)
        }

    override suspend fun consumirBolsaCafe(
        bolsaId: String,
        gramos: Double
    ): Result<BolsaCafe> = withAuthContext { token ->
        val dto = remoteDataSource.consumirBolsaCafe(
            token,
            ConsumirBolsaCafeRequest(
                bolsaId = bolsaId,
                gramos = gramos
            )
        )
        BolsaCafeMapper.toDomain(dto)
    }

    override suspend fun crearBolsaCafe(input: BolsaCafeInput): Result<BolsaCafe> =
        withAuthContext { token ->
            val dto = remoteDataSource.crearBolsaCafe(
                token,
                CreateBolsaCafeRequest(
                    nombre = input.nombre,
                    pesoInicial = input.pesoInicial,
                    pesoRestante = input.pesoRestante,
                    origen = input.origen,
                    tostador = input.tostador,
                    varietal = input.varietal,
                    notas = input.notas,
                    moliendaSugerida = input.moliendaSugerida
                )
            )
            BolsaCafeMapper.toDomain(dto)
        }

    private fun inferirDificultad(extraccion: Extraccion): Dificultad {
        val tiempo = extraccion.tiempoExtraccion ?: return Dificultad.INTERMEDIO
        return when {
            tiempo <= 120 -> Dificultad.FACIL
            tiempo <= 300 -> Dificultad.INTERMEDIO
            else -> Dificultad.AVANZADO
        }
    }

    private suspend fun <T> withAuthContext(
        block: suspend (token: String) -> T
    ): Result<T> = withContext(Dispatchers.IO) {
        val token = authRepository.getAuthToken()
        if (token.isNullOrBlank()) {
            return@withContext Result.failure<T>(
                IllegalStateException("Sesión no disponible. Inicia sesión nuevamente.")
            )
        }
        runCatching { block(token) }.mapError()
    }

    private fun <T> Result<T>.mapError(): Result<T> = this.mapError { throwable ->
        when (throwable) {
            is HttpException -> {
                val message = when (throwable.code()) {
                    404 -> "No se encontraron datos de extracción."
                    401 -> "Sesión expirada. Inicia sesión otra vez."
                    else -> "Error del servidor (${throwable.code()})."
                }
                Exception(message, throwable)
            }
            is IOException -> Exception("Problemas de conexión. Revisa tu red.", throwable)
            else -> throwable
        }
    }

    private inline fun <T> Result<T>.mapError(
        crossinline transform: (Throwable) -> Throwable
    ): Result<T> = fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(transform(it)) }
    )
}

