package com.upc.xantina.features.social.domain.model

data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val userName: String,
    val content: String,
    val parentCommentId: String? = null,
    val repliesCount: Int = 0,
    val createdAt: String,
    val updatedAt: String? = null
)

