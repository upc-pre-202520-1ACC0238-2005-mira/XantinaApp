package com.upc.xantina.features.social.infrastructure.api.dto

import com.google.gson.annotations.SerializedName

data class PostDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("authorId")
    val authorId: String,
    
    @SerializedName("authorName")
    val authorName: String,
    
    @SerializedName("authorEmail")
    val authorEmail: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    
    @SerializedName("extractionId")
    val extractionId: String? = null,
    
    @SerializedName("likesCount")
    val likesCount: Int = 0,
    
    @SerializedName("commentsCount")
    val commentsCount: Int = 0,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class CreatePostRequestDto(
    @SerializedName("content")
    val content: String,
    
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    
    @SerializedName("extractionId")
    val extractionId: String? = null
)

data class ToggleLikeResponseDto(
    @SerializedName("liked")
    val liked: Boolean
)

data class CheckLikedResponseDto(
    @SerializedName("liked")
    val liked: Boolean
)

