package com.upc.xantina.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upc.xantina.core.domain.model.User
import com.upc.xantina.core.domain.repository.AuthRepository
import com.upc.xantina.features.auth.presentation.state.AuthAction
import com.upc.xantina.features.auth.presentation.state.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.login(email, password)
            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = user,
                        lastAction = AuthAction.LOGIN,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Ha ocurrido un error inesperado."
                    )
                }
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.register(name, email, password)
            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = user,
                        lastAction = AuthAction.REGISTER,
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Ha ocurrido un error inesperado."
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun consumeAuthAction() {
        _uiState.update { it.copy(lastAction = null) }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState()
        }
    }

    fun restoreSession(user: User) {
        _uiState.update {
            it.copy(
                isAuthenticated = true,
                user = user,
                isLoading = false,
                errorMessage = null
            )
        }
    }

}

