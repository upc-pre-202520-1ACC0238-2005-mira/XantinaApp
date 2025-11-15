package com.upc.xantina.features.social.presentation.state

import com.upc.xantina.features.social.domain.model.PostExtractionData

sealed class ExtractionDataUiState {
    object Idle : ExtractionDataUiState()
    object Loading : ExtractionDataUiState()
    data class Success(val data: PostExtractionData) : ExtractionDataUiState()
    data class Error(val message: String) : ExtractionDataUiState()
}



