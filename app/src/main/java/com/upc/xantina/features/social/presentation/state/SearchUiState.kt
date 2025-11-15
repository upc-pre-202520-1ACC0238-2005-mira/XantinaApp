package com.upc.xantina.features.social.presentation.state

import com.upc.xantina.features.social.domain.model.UserSearchResult

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val users: List<UserSearchResult>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}



