package com.upc.xantina.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upc.xantina.features.profile.domain.repository.ProfileRepository
import com.upc.xantina.features.profile.presentation.state.ChangePasswordState
import com.upc.xantina.features.profile.presentation.state.ProfileUiState
import com.upc.xantina.features.profile.presentation.state.UpdateProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para gestionar el estado de la pantalla de perfil
 * Implementa la lógica de presentación siguiendo arquitectura DDD
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    
    // Estado principal del perfil
    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Initial)
    val profileState = _profileState.asStateFlow()
    
    // Estado de actualización de perfil
    private val _updateProfileState = MutableStateFlow<UpdateProfileState>(UpdateProfileState.Idle)
    val updateProfileState = _updateProfileState.asStateFlow()
    
    // Estado de cambio de contraseña
    private val _changePasswordState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val changePasswordState = _changePasswordState.asStateFlow()
    
    /**
     * Carga el perfil del usuario autenticado
     */
    fun loadProfile(token: String) {
        viewModelScope.launch {
            _profileState.value = ProfileUiState.Loading
            
            profileRepository.getProfile(token)
                .onSuccess { user ->
                    _profileState.value = ProfileUiState.Success(user)
                }
                .onFailure { exception ->
                    _profileState.value = ProfileUiState.Error(
                        exception.message ?: "Error al cargar el perfil"
                    )
                }
        }
    }
    
    /**
     * Actualiza el perfil del usuario
     */
    fun updateProfile(token: String, name: String? = null, email: String? = null) {
        // Validación básica
        if (name.isNullOrBlank() && email.isNullOrBlank()) {
            _updateProfileState.value = UpdateProfileState.Error("Debes proporcionar al menos un campo para actualizar")
            return
        }
        
        viewModelScope.launch {
            _updateProfileState.value = UpdateProfileState.Loading
            
            profileRepository.updateProfile(token, name, email)
                .onSuccess { user ->
                    _updateProfileState.value = UpdateProfileState.Success(user)
                    // También actualizamos el estado principal
                    _profileState.value = ProfileUiState.Success(user)
                }
                .onFailure { exception ->
                    _updateProfileState.value = UpdateProfileState.Error(
                        exception.message ?: "Error al actualizar el perfil"
                    )
                }
        }
    }
    
    /**
     * Cambia la contraseña del usuario
     */
    fun changePassword(token: String, currentPassword: String, newPassword: String) {
        // Validaciones
        if (currentPassword.isBlank()) {
            _changePasswordState.value = ChangePasswordState.Error("La contraseña actual es requerida")
            return
        }
        
        if (newPassword.length < 6) {
            _changePasswordState.value = ChangePasswordState.Error("La nueva contraseña debe tener al menos 6 caracteres")
            return
        }
        
        if (currentPassword == newPassword) {
            _changePasswordState.value = ChangePasswordState.Error("La nueva contraseña debe ser diferente a la actual")
            return
        }
        
        viewModelScope.launch {
            _changePasswordState.value = ChangePasswordState.Loading
            
            profileRepository.changePassword(token, currentPassword, newPassword)
                .onSuccess { message ->
                    _changePasswordState.value = ChangePasswordState.Success(message)
                }
                .onFailure { exception ->
                    _changePasswordState.value = ChangePasswordState.Error(
                        exception.message ?: "Error al cambiar la contraseña"
                    )
                }
        }
    }
    
    /**
     * Resetea el estado de actualización de perfil
     */
    fun resetUpdateState() {
        _updateProfileState.value = UpdateProfileState.Idle
    }
    
    /**
     * Resetea el estado de cambio de contraseña
     */
    fun resetPasswordState() {
        _changePasswordState.value = ChangePasswordState.Idle
    }
}
