package com.upc.xantina.features.social.infrastructure.api

import com.upc.xantina.features.social.infrastructure.api.dto.*
import retrofit2.http.*

interface SocialApiService {
    
    // Posts
    @POST("social/posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Body request: CreatePostRequestDto
    ): PostDto
    
    @GET("social/posts/feed")
    suspend fun getFeed(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): List<PostDto>
    
    @GET("social/posts/{postId}")
    suspend fun getPostById(
        @Header("Authorization") token: String,
        @Path("postId") postId: String
    ): PostDto
    
    @GET("social/posts/user/{userId}")
    suspend fun getUserPosts(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): List<PostDto>
    
    @DELETE("social/posts/{postId}")
    suspend fun deletePost(
        @Header("Authorization") token: String,
        @Path("postId") postId: String
    )
    
    // Likes
    @POST("social/posts/{postId}/like")
    suspend fun toggleLike(
        @Header("Authorization") token: String,
        @Path("postId") postId: String
    ): ToggleLikeResponseDto
    
    @GET("social/posts/{postId}/liked")
    suspend fun checkUserLiked(
        @Header("Authorization") token: String,
        @Path("postId") postId: String
    ): CheckLikedResponseDto
    
    // Comments
    @POST("social/posts/{postId}/comments")
    suspend fun createComment(
        @Header("Authorization") token: String,
        @Path("postId") postId: String,
        @Body request: CreateCommentRequestDto
    ): CommentDto
    
    @GET("social/posts/{postId}/comments")
    suspend fun getPostComments(
        @Header("Authorization") token: String,
        @Path("postId") postId: String
    ): List<CommentDto>
    
    @GET("social/comments/{commentId}/replies")
    suspend fun getCommentReplies(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: String
    ): List<CommentDto>
    
    @DELETE("social/comments/{commentId}")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: String
    )
}

