package com.upc.xantina.features.auth.presentation.state

import com.upc.xantina.core.domain.model.User

/**
 * Estado expuesto por la pantalla de autenticación.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val lastAction: AuthAction? = null
)

/**
 * Identifica la acción de autenticación que disparó el último estado exitoso.
 */
enum class AuthAction {
    LOGIN,
    REGISTER
}







