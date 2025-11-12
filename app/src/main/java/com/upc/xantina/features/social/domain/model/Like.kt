package com.upc.xantina.features.social.domain.model

data class Like(
    val id: String,
    val postId: String,
    val userId: String,
    val createdAt: String
)

