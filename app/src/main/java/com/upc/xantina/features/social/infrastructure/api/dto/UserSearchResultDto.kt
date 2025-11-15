package com.upc.xantina.features.social.infrastructure.api.dto

import com.google.gson.annotations.SerializedName

data class UserSearchResultDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("isFollowing")
    val isFollowing: Boolean,
    @SerializedName("followersCount")
    val followersCount: Int
)

