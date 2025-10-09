package com.upc.xantina.features.auth.domain.usecase

import com.upc.xantina.core.domain.model.User
import com.upc.xantina.core.domain.repository.AuthRepository

/**
 * Caso de uso para iniciar sesión
 * Encapsula la lógica de negocio para el login
 */
class LoginUseCase(
    private val authRepository: AuthRepository
) {
    
    /**
     * Ejecuta el caso de uso de login
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Result<User> con el usuario autenticado o error
     */
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<User> {
        
        // Validaciones de negocio
        if (email.isBlank()) {
            return Result.failure(Exception("El email es requerido"))
        }
        
        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña es requerida"))
        }
        
        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        
        // Validar formato de email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("El formato del email no es válido"))
        }
        
        return authRepository.login(email.trim(), password)
    }
}
