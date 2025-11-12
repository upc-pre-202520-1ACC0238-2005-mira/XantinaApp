package com.upc.xantina.features.social.domain.repository

import com.upc.xantina.features.social.domain.model.Comment
import com.upc.xantina.features.social.domain.model.Post

interface SocialRepository {
    
    // Posts
    suspend fun createPost(
        token: String,
        content: String,
        imageUrl: String? = null,
        extractionId: String? = null
    ): Result<Post>
    
    suspend fun getFeed(
        token: String,
        limit: Int = 20,
        offset: Int = 0
    ): Result<List<Post>>
    
    suspend fun getPostById(token: String, postId: String): Result<Post>
    
    suspend fun getUserPosts(token: String, userId: String): Result<List<Post>>
    
    suspend fun deletePost(token: String, postId: String): Result<Unit>
    
    // Likes
    suspend fun toggleLike(token: String, postId: String): Result<Boolean>
    
    suspend fun checkUserLiked(token: String, postId: String): Result<Boolean>
    
    // Comments
    suspend fun createComment(
        token: String,
        postId: String,
        content: String,
        parentCommentId: String? = null
    ): Result<Comment>
    
    suspend fun getPostComments(token: String, postId: String): Result<List<Comment>>
    
    suspend fun getCommentReplies(token: String, commentId: String): Result<List<Comment>>
    
    suspend fun deleteComment(token: String, commentId: String): Result<Unit>
}

