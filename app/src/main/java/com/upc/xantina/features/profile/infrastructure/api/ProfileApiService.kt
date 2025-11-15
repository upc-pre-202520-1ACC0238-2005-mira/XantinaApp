package com.upc.xantina.features.profile.infrastructure.api

import com.upc.xantina.features.profile.infrastructure.api.dto.ChangePasswordRequestDto
import com.upc.xantina.features.profile.infrastructure.api.dto.ChangePasswordResponseDto
import com.upc.xantina.features.profile.infrastructure.api.dto.UpdateProfileRequestDto
import com.upc.xantina.features.profile.infrastructure.api.dto.UserProfileDto
import retrofit2.http.*

interface ProfileApiService {
    
    @GET("auth/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): UserProfileDto
    
    @PUT("auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequestDto
    ): UserProfileDto
    
    @PATCH("auth/profile/password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequestDto
    ): ChangePasswordResponseDto
}

