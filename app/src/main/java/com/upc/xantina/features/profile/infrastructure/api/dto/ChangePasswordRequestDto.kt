package com.upc.xantina.features.profile.infrastructure.api.dto

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequestDto(
    @SerializedName("currentPassword")
    val currentPassword: String,
    
    @SerializedName("newPassword")
    val newPassword: String
)
