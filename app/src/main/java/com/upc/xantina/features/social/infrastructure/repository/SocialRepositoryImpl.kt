package com.upc.xantina.features.social.infrastructure.repository

import com.upc.xantina.features.social.domain.model.Comment
import com.upc.xantina.features.social.domain.model.Post
import com.upc.xantina.features.social.domain.repository.SocialRepository
import com.upc.xantina.features.social.infrastructure.api.SocialApiService
import com.upc.xantina.features.social.infrastructure.api.dto.CreateCommentRequestDto
import com.upc.xantina.features.social.infrastructure.api.dto.CreatePostRequestDto
import com.upc.xantina.features.social.infrastructure.mapper.SocialMapper.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SocialRepositoryImpl @Inject constructor(
    private val apiService: SocialApiService
) : SocialRepository {
    
    override suspend fun createPost(
        token: String,
        content: String,
        imageUrl: String?,
        extractionId: String?
    ): Result<Post> = withContext(Dispatchers.IO) {
        try {
            val request = CreatePostRequestDto(content, imageUrl, extractionId)
            val response = apiService.createPost("Bearer $token", request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFeed(
        token: String,
        limit: Int,
        offset: Int
    ): Result<List<Post>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getFeed("Bearer $token", limit, offset)
            // Check liked status for each post
            val postsWithLikes = response.map { postDto ->
                try {
                    val liked = apiService.checkUserLiked("Bearer $token", postDto.id)
                    postDto.toDomain(liked.liked)
                } catch (e: Exception) {
                    postDto.toDomain(false)
                }
            }
            Result.success(postsWithLikes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPostById(token: String, postId: String): Result<Post> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPostById("Bearer $token", postId)
                val liked = try {
                    apiService.checkUserLiked("Bearer $token", postId).liked
                } catch (e: Exception) {
                    false
                }
                Result.success(response.toDomain(liked))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    override suspend fun getUserPosts(token: String, userId: String): Result<List<Post>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUserPosts("Bearer $token", userId)
                val posts = response.map { it.toDomain() }
                Result.success(posts)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    override suspend fun deletePost(token: String, postId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                apiService.deletePost("Bearer $token", postId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    override suspend fun toggleLike(token: String, postId: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.toggleLike("Bearer $token", postId)
                Result.success(response.liked)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    override suspend fun checkUserLiked(token: String, postId: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.checkUserLiked("Bearer $token", postId)
                Result.success(response.liked)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    override suspend fun createComment(
        token: String,
        postId: String,
        content: String,
        parentCommentId: String?
    ): Result<Comment> = withContext(Dispatchers.IO) {
        try {
            val request = CreateCommentRequestDto(content, parentCommentId)
            val response = apiService.createComment("Bearer $token", postId, request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPostComments(token: String, postId: String): Result<List<Comment>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPostComments("Bearer $token", postId)
                val comments = response.map { it.toDomain() }
                Result.success(comments)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    override suspend fun getCommentReplies(
        token: String,
        commentId: String
    ): Result<List<Comment>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCommentReplies("Bearer $token", commentId)
            val replies = response.map { it.toDomain() }
            Result.success(replies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteComment(token: String, commentId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                apiService.deleteComment("Bearer $token", commentId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

