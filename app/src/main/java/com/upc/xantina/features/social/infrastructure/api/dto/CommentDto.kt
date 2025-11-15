package com.upc.xantina.features.social.infrastructure.api.dto

import com.google.gson.annotations.SerializedName

data class CommentDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("postId")
    val postId: String,
    
    @SerializedName("userId")
    val userId: String,
    
    @SerializedName("userName")
    val userName: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("parentCommentId")
    val parentCommentId: String? = null,
    
    @SerializedName("repliesCount")
    val repliesCount: Int = 0,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class CreateCommentRequestDto(
    @SerializedName("content")
    val content: String,
    
    @SerializedName("parentCommentId")
    val parentCommentId: String? = null
)

