package com.upc.xantina.features.auth.infrastructure.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.upc.xantina.core.domain.model.User
import com.upc.xantina.core.domain.repository.AuthRepository
import com.upc.xantina.features.auth.infrastructure.datasource.remote.AuthRemoteDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de autenticación.
 * Orquesta la obtención de datos remotos y la persistencia local.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("xantina_auth", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    private var cachedUser: User? = null
    private var cachedToken: String? = null
    
    init {
        // Cargar sesión guardada al inicializar
        loadSavedSession()
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<User> = runCatching {
        val response = remoteDataSource.register(name, email, password)
        val user = response.user.toDomain()
        cacheSession(response.accessToken, user)
        user
    }.mapError()

    override suspend fun login(
        email: String,
        password: String
    ): Result<User> = runCatching {
        val response = remoteDataSource.login(email, password)
        val user = response.user.toDomain()
        cacheSession(response.accessToken, user)
        user
    }.mapError()

    override suspend fun getCurrentUser(): User? {
        if (cachedUser == null) {
            loadSavedSession()
        }
        return cachedUser
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        cachedUser = null
        cachedToken = null
        // Limpiar SharedPreferences
        prefs.edit().clear().apply()
    }.mapError()

    override suspend fun isUserAuthenticated(): Boolean {
        if (cachedToken == null) {
            loadSavedSession()
        }
        return cachedToken != null
    }
    
    override suspend fun getAuthToken(): String? {
        if (cachedToken == null) {
            loadSavedSession()
        }
        return cachedToken
    }
    
    private fun loadSavedSession() {
        val savedToken = prefs.getString("auth_token", null)
        val savedUserJson = prefs.getString("auth_user", null)
        
        if (savedToken != null && savedUserJson != null) {
            try {
                cachedToken = savedToken
                cachedUser = gson.fromJson(savedUserJson, User::class.java)
            } catch (e: Exception) {
                // Si hay error al parsear, limpiar la sesión
                prefs.edit().clear().apply()
                cachedToken = null
                cachedUser = null
            }
        }
    }

    private fun <T> Result<T>.mapError(): Result<T> = this.mapError { throwable ->
        when (throwable) {
            is HttpException -> {
                val message = when (throwable.code()) {
                    400 -> "Solicitud inválida. Verifica tus datos."
                    401 -> "Credenciales incorrectas."
                    409 -> "El correo ya está registrado."
                    else -> "Ha ocurrido un error con el servidor (${throwable.code()})."
                }
                Exception(message, throwable)
            }
            is IOException -> Exception("Problemas de conexión. Revisa tu red.", throwable)
            else -> throwable
        }
    }

    private inline fun <T> Result<T>.mapError(crossinline transform: (Throwable) -> Throwable): Result<T> {
        return fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(transform(it)) }
        )
    }

    private fun cacheSession(token: String, user: User) {
        cachedToken = token
        cachedUser = user
        
        // Guardar en SharedPreferences para persistencia
        prefs.edit().apply {
            putString("auth_token", token)
            putString("auth_user", gson.toJson(user))
            apply()
        }
    }
}

