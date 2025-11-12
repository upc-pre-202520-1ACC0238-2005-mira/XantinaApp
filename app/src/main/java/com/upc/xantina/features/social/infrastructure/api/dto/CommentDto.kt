package com.upc.xantina.features.social.infrastructure.api.dto

import com.google.gson.annotations.SerializedName

data class CommentDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("postId")
    val postId: String,
    
    @SerializedName("authorId")
    val authorId: String,
    
    @SerializedName("authorName")
    val authorName: String,
    
    @SerializedName("authorEmail")
    val authorEmail: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("parentId")
    val parentId: String? = null,
    
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
    
    @SerializedName("parentId")
    val parentId: String? = null
)

