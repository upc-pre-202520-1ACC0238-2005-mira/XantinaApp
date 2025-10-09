package com.upc.xantina.core.domain.repository

import com.upc.xantina.core.domain.model.User

/**
 * Interface del repositorio de autenticación
 * Define los contratos para las operaciones de autenticación
 */
interface AuthRepository {
    
    /**
     * Registra un nuevo usuario
     * @param name Nombre del usuario
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Result<User> con el usuario creado o error
     */
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<User>
    
    /**
     * Inicia sesión con email y contraseña
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Result<User> con el usuario autenticado o error
     */
    suspend fun login(
        email: String,
        password: String
    ): Result<User>
    
    /**
     * Obtiene el usuario actual autenticado
     * @return User? el usuario actual o null si no está autenticado
     */
    suspend fun getCurrentUser(): User?
    
    /**
     * Cierra la sesión del usuario actual
     */
    suspend fun logout(): Result<Unit>
    
    /**
     * Verifica si hay un usuario autenticado
     * @return Boolean true si hay usuario autenticado
     */
    suspend fun isUserAuthenticated(): Boolean
}