package com.upc.xantina.features.social.infrastructure.mapper

import com.upc.xantina.features.social.domain.model.Comment
import com.upc.xantina.features.social.domain.model.Post
import com.upc.xantina.features.social.domain.model.UserSearchResult
import com.upc.xantina.features.social.domain.model.PostExtractionData
import com.upc.xantina.features.social.domain.model.ConfiguracionMetodo
import com.upc.xantina.features.social.domain.model.BaseConfig
import com.upc.xantina.features.social.domain.model.PasoExtraccion
import com.upc.xantina.features.social.infrastructure.api.dto.CommentDto
import com.upc.xantina.features.social.infrastructure.api.dto.PostDto
import com.upc.xantina.features.social.infrastructure.api.dto.UserSearchResultDto
import com.upc.xantina.features.social.infrastructure.api.dto.PostExtractionDataDto

object SocialMapper {
    
    fun PostDto.toDomain(isLikedByCurrentUser: Boolean = false): Post {
        return Post(
            id = this.id,
            userId = this.userId,
            userName = this.userName,
            userEmail = this.userEmail,
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
            userId = this.userId,
            userName = this.userName,
            content = this.content,
            parentCommentId = this.parentCommentId,
            repliesCount = this.repliesCount,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
    
    fun UserSearchResultDto.toDomain(): UserSearchResult {
        return UserSearchResult(
            id = this.id,
            name = this.name,
            email = this.email,
            role = this.role,
            isFollowing = this.isFollowing,
            followersCount = this.followersCount
        )
    }
    
    fun PostExtractionDataDto.toDomain(): PostExtractionData {
        return PostExtractionData(
            recetaId = this.recetaId,
            nombre = this.nombre,
            metodo = this.metodo,
            ratio = this.ratio,
            gramosCafe = this.gramosCafe,
            mililitrosAgua = this.mililitrosAgua,
            temperaturaAgua = this.temperaturaAgua,
            tiempoExtraccion = this.tiempoExtraccion,
            configuracion = this.configuracion?.toDomain(),
            notas = this.notas
        )
    }
    
    fun com.upc.xantina.features.social.infrastructure.api.dto.ConfiguracionMetodoDto.toDomain(): ConfiguracionMetodo {
        return ConfiguracionMetodo(
            grind = this.grind,
            temperature = this.temperature,
            base = this.base?.toDomain(),
            totalTimeSeconds = this.totalTimeSeconds,
            steps = this.steps?.map { it.toDomain() }
        )
    }
    
    fun com.upc.xantina.features.social.infrastructure.api.dto.BaseConfigDto.toDomain(): BaseConfig {
        return BaseConfig(
            cafeG = this.cafeG,
            aguaTotalMl = this.aguaTotalMl
        )
    }
    
    fun com.upc.xantina.features.social.infrastructure.api.dto.PasoExtraccionDto.toDomain(): PasoExtraccion {
        return PasoExtraccion(
            step = this.step,
            timeStart = this.timeStart,
            timeEnd = this.timeEnd,
            action = this.action,
            waterMl = this.waterMl,
            calculation = this.calculation
        )
    }
}

