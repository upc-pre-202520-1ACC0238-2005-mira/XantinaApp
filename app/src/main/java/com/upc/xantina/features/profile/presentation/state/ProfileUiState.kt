package com.upc.xantina.features.profile.presentation.state

import com.upc.xantina.core.domain.model.User

/**
 * Estados de UI para la pantalla de perfil
 * Sigue el patrón de estados recomendado en la arquitectura DDD
 */
sealed class ProfileUiState {
    object Initial : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

/**
 * Estados para actualización de perfil
 */
sealed class UpdateProfileState {
    object Idle : UpdateProfileState()
    object Loading : UpdateProfileState()
    data class Success(val user: User) : UpdateProfileState()
    data class Error(val message: String) : UpdateProfileState()
}

/**
 * Estados para cambio de contraseña
 */
sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Loading : ChangePasswordState()
    data class Success(val message: String) : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}
