package com.upc.xantina.features.profile.infrastructure.repository

import com.upc.xantina.core.domain.model.User
import com.upc.xantina.features.profile.domain.repository.ProfileRepository
import com.upc.xantina.features.profile.infrastructure.api.ProfileApiService
import com.upc.xantina.features.profile.infrastructure.api.dto.ChangePasswordRequestDto
import com.upc.xantina.features.profile.infrastructure.api.dto.UpdateProfileRequestDto
import com.upc.xantina.features.profile.infrastructure.mapper.ProfileMapper.toDomain
import javax.inject.Inject

/**
 * Implementación del ProfileRepository
 * Gestiona las llamadas al API y convierte los DTOs a entidades de dominio
 */
class ProfileRepositoryImpl @Inject constructor(
    private val apiService: ProfileApiService
) : ProfileRepository {
    
    override suspend fun getProfile(token: String): Result<User> {
        return try {
            val response = apiService.getProfile("Bearer $token")
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateProfile(
        token: String,
        name: String?,
        email: String?
    ): Result<User> {
        return try {
            val request = UpdateProfileRequestDto(
                name = name,
                email = email
            )
            val response = apiService.updateProfile("Bearer $token", request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun changePassword(
        token: String,
        currentPassword: String,
        newPassword: String
    ): Result<String> {
        return try {
            val request = ChangePasswordRequestDto(
                currentPassword = currentPassword,
                newPassword = newPassword
            )
            val response = apiService.changePassword("Bearer $token", request)
            Result.success(response.message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

