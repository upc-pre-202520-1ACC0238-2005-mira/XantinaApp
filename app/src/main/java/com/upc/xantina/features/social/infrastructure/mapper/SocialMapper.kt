package com.upc.xantina.features.social.infrastructure.mapper

import com.upc.xantina.features.social.domain.model.Comment
import com.upc.xantina.features.social.domain.model.Post
import com.upc.xantina.features.social.infrastructure.api.dto.CommentDto
import com.upc.xantina.features.social.infrastructure.api.dto.PostDto

object SocialMapper {
    
    fun PostDto.toDomain(isLikedByCurrentUser: Boolean = false): Post {
        return Post(
            id = this.id,
            userId = this.authorId,
            userName = this.authorName,
            userEmail = this.authorEmail,
            content = this.content,
            imageUrl = this.imageUrl,
            extractionId = this.extractionId,
            likesCount = this.likesCount,
            commentsCount = this.commentsCount,
            isLikedByCurrentUser = isLikedByCurrentUser,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
    
    fun CommentDto.toDomain(): Comment {
        return Comment(
            id = this.id,
            postId = this.postId,
            userId = this.authorId,
            userName = this.authorName,
            content = this.content,
            parentCommentId = this.parentId,
            repliesCount = this.repliesCount,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}

