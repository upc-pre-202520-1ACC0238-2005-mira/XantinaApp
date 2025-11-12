package com.upc.xantina.features.auth.infrastructure.api.dto

import com.google.gson.annotations.SerializedName
import com.upc.xantina.core.domain.model.User

/**
 * DTO que representa al usuario retornado por los endpoints de autenticación.
 */
data class UserResponseDto(
    val id: String?,
    val name: String,
    val email: String,
    @SerializedName("role")
    val role: String = "user"
) {
    fun toDomain(): User = User(
        id = id,
        name = name,
        email = email,
        role = role
    )
}




