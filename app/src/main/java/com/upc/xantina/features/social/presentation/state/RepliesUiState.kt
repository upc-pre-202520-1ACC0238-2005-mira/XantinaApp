package com.upc.xantina.features.social.presentation.state

import com.upc.xantina.features.social.domain.model.Comment

sealed class RepliesUiState {
    object Idle : RepliesUiState()
    object Loading : RepliesUiState()
    data class Success(val replies: List<Comment>) : RepliesUiState()
    data class Error(val message: String) : RepliesUiState()
}

