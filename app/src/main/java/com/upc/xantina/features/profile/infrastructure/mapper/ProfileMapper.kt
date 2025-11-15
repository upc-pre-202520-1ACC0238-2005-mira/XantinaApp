package com.upc.xantina.features.profile.infrastructure.mapper

import com.upc.xantina.core.domain.model.User
import com.upc.xantina.features.profile.infrastructure.api.dto.UserProfileDto

/**
 * Mapper para convertir DTOs de perfil a entidades de dominio
 */
object ProfileMapper {
    
    /**
     * Convierte UserProfileDto a User (entidad de dominio)
     */
    fun UserProfileDto.toDomain(): User {
        return User(
            id = this.id,
            name = this.name,
            email = this.email,
            role = this.role
        )
    }
}

