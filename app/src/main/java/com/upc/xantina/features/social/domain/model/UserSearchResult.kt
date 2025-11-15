package com.upc.xantina.features.social.domain.model

data class UserSearchResult(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val isFollowing: Boolean,
    val followersCount: Int
)



