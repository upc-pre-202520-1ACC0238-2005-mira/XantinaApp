package com.upc.xantina.features.social.domain.model

data class Post(
    val id: String,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val content: String,
    val imageUrl: String? = null,
    val extractionId: String? = null,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLikedByCurrentUser: Boolean = false,
    val createdAt: String,
    val updatedAt: String? = null
)

