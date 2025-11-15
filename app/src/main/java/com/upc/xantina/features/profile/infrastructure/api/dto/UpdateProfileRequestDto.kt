package com.upc.xantina.features.profile.infrastructure.api.dto

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequestDto(
    @SerializedName("name")
    val name: String? = null,
    
    @SerializedName("email")
    val email: String? = null
)
