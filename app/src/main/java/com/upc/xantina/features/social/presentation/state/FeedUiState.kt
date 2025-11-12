package com.upc.xantina.features.social.presentation.state

import com.upc.xantina.features.social.domain.model.Post

sealed class FeedUiState {
    object Idle : FeedUiState()
    object Loading : FeedUiState()
    data class Success(val posts: List<Post>) : FeedUiState()
    data class Error(val message: String) : FeedUiState()
}

