package com.upc.xantina.core.domain.model

/**
 * Entidad de dominio User
 * Representa un usuario en el sistema con toda su lógica de negocio
 */
data class User(
    val id: String? = null,
    val name: String,
    val email: String,
    val role: String = "user"
) {
    /**
     * Valida si el email tiene formato correcto
     */
    fun isValidEmail(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Valida si el nombre es válido (no vacío y mínimo 2 caracteres)
     */
    fun isValidName(): Boolean {
        return name.trim().length >= 2
    }
    
    /**
     * Obtiene el nombre formateado (primera letra mayúscula)
     */
    fun getFormattedName(): String {
        return name.trim().split(" ")
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { 
                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                }
            }
    }
}