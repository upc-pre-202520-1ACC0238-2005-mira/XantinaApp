package com.upc.xantina.features.social.presentation.state

import com.upc.xantina.features.social.domain.model.Comment

sealed class CommentsUiState {
    object Idle : CommentsUiState()
    object Loading : CommentsUiState()
    data class Success(val comments: List<Comment>) : CommentsUiState()
    data class Error(val message: String) : CommentsUiState()
}

