package com.upc.xantina.features.profile.domain.repository

import com.upc.xantina.core.domain.model.User

/**
 * Repository Interface para operaciones de perfil de usuario
 * Define el contrato para la capa de datos
 */
interface ProfileRepository {
    
    /**
     * Obtiene el perfil del usuario autenticado
     * @param token Token de autenticación JWT
     * @return Result con User o excepción
     */
    suspend fun getProfile(token: String): Result<User>
    
    /**
     * Actualiza el perfil del usuario
     * @param token Token de autenticación JWT
     * @param name Nuevo nombre (opcional)
     * @param email Nuevo email (opcional)
     * @return Result con User actualizado o excepción
     */
    suspend fun updateProfile(
        token: String,
        name: String? = null,
        email: String? = null
    ): Result<User>
    
    /**
     * Cambia la contraseña del usuario
     * @param token Token de autenticación JWT
     * @param currentPassword Contraseña actual
     * @param newPassword Nueva contraseña
     * @return Result con mensaje de éxito o excepción
     */
    suspend fun changePassword(
        token: String,
        currentPassword: String,
        newPassword: String
    ): Result<String>
}

