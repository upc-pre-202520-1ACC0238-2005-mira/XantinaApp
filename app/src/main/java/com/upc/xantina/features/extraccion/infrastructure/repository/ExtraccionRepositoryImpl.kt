package com.upc.xantina.features.extraccion.infrastructure.repository

import com.upc.xantina.features.extraccion.data.mapper.ExtraccionMapper
import com.upc.xantina.features.extraccion.domain.model.Dificultad
import com.upc.xantina.features.extraccion.domain.model.Extraccion
import com.upc.xantina.features.extraccion.domain.model.MetodoExtraccion
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
    private val remoteDataSource: ExtraccionRemoteDataSource
) : ExtraccionRepository {

    override suspend fun getMetodosExtraccion(): Result<List<MetodoExtraccion>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val extraccionesDto = remoteDataSource.obtenerExtracciones()
                val extracciones = ExtraccionMapper.toDomainList(extraccionesDto)

                extracciones
                    .groupBy { it.metodoExtraccion.lowercase() }
                    .map { (_, items) ->
                        val primero = items.first()
                        MetodoExtraccion(
                            id = primero.metodoExtraccion,
                            nombre = primero.metodoExtraccion,
                            descripcion = primero.notas ?: "Última receta: ${primero.nombreCafe}",
                            tiempoPreparacion = primero.tiempoExtraccion?.let { "$it s" } ?: "—",
                            icono = primero.metodoExtraccion,
                            dificultad = inferirDificultad(primero),
                            temperatura = primero.temperaturaAgua,
                            ratio = primero.calcularRatio()
                        )
                    }
                    .sortedBy { it.nombre }
            }.mapError()
        }

    override suspend fun getMetodoExtraccionById(id: String): Result<MetodoExtraccion?> =
        withContext(Dispatchers.IO) {
            runCatching {
                val extracciones = remoteDataSource.obtenerExtracciones(metodo = id)
                extracciones.firstOrNull()?.let { dto ->
                    val dominio = ExtraccionMapper.toDomain(dto)
                    MetodoExtraccion(
                        id = dominio.metodoExtraccion,
                        nombre = dominio.metodoExtraccion,
                        descripcion = dominio.notas ?: "Receta destacada: ${dominio.nombreCafe}",
                        tiempoPreparacion = dominio.tiempoExtraccion?.let { "$it s" } ?: "—",
                        icono = dominio.metodoExtraccion,
                        dificultad = inferirDificultad(dominio),
                        temperatura = dominio.temperaturaAgua,
                        ratio = dominio.calcularRatio()
                    )
                }
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

    override suspend fun guardarExtraccion(extraccion: Extraccion): Result<Extraccion> {
        return Result.failure(UnsupportedOperationException("No implementado aún"))
    }

    override suspend fun actualizarExtraccion(extraccion: Extraccion): Result<Extraccion> {
        return Result.failure(UnsupportedOperationException("No implementado aún"))
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

    private fun inferirDificultad(extraccion: Extraccion): Dificultad {
        val tiempo = extraccion.tiempoExtraccion ?: return Dificultad.INTERMEDIO
        return when {
            tiempo <= 120 -> Dificultad.FACIL
            tiempo <= 300 -> Dificultad.INTERMEDIO
            else -> Dificultad.AVANZADO
        }
    }

    private fun <T> Result<T>.mapError(): Result<T> = this.mapError { throwable ->
        when (throwable) {
            is HttpException -> {
                val message = when (throwable.code()) {
                    404 -> "No se encontraron datos de extracción."
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

