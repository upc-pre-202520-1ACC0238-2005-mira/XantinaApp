package com.upc.xantina.features.auth.domain.usecase

import com.upc.xantina.core.domain.model.User
import com.upc.xantina.core.domain.repository.AuthRepository

/**
 * Caso de uso para registrar un nuevo usuario
 * Encapsula la lógica de negocio para el registro
 */
class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    
    /**
     * Ejecuta el caso de uso de registro
     * @param name Nombre del usuario
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Result<User> con el usuario creado o error
     */
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String
    ): Result<User> {
        
        // Validaciones de negocio
        if (name.isBlank()) {
            return Result.failure(Exception("El nombre es requerido"))
        }
        
        if (email.isBlank()) {
            return Result.failure(Exception("El email es requerido"))
        }
        
        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña es requerida"))
        }
        
        // Validar longitud del nombre
        if (name.trim().length < 2) {
            return Result.failure(Exception("El nombre debe tener al menos 2 caracteres"))
        }
        
        // Validar longitud de la contraseña
        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        
        // Validar formato de email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("El formato del email no es válido"))
        }
        
        // Validar que el nombre no contenga solo espacios
        if (name.trim().isEmpty()) {
            return Result.failure(Exception("El nombre no puede estar vacío"))
        }
        
        return authRepository.register(
            name = name.trim(),
            email = email.trim().lowercase(),
            password = password
        )
    }
}
