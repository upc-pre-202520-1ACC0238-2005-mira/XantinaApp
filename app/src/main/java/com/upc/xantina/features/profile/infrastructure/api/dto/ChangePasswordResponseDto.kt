package com.upc.xantina.features.profile.infrastructure.api.dto

import com.google.gson.annotations.SerializedName

data class ChangePasswordResponseDto(
    @SerializedName("message")
    val message: String
)
