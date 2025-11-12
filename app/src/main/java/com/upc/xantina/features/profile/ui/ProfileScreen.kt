package com.upc.xantina.features.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.upc.xantina.core.domain.repository.AuthRepository
import com.upc.xantina.features.profile.presentation.state.ChangePasswordState
import com.upc.xantina.features.profile.presentation.state.ProfileUiState
import com.upc.xantina.features.profile.presentation.state.UpdateProfileState
import com.upc.xantina.features.profile.presentation.viewmodel.ProfileViewModel
import com.upc.xantina.shared.ui.components.XantinaButton
import com.upc.xantina.shared.ui.components.XantinaTextField
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    authRepository: AuthRepository,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val profileState by viewModel.profileState.collectAsState()
    val updateProfileState by viewModel.updateProfileState.collectAsState()
    val changePasswordState by viewModel.changePasswordState.collectAsState()
    
    var showEditDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    
    // Cargar perfil al iniciar
    LaunchedEffect(Unit) {
        scope.launch {
            val token = authRepository.getAuthToken()
            if (token != null) {
                viewModel.loadProfile(token)
            }
        }
    }
    
    // Mostrar mensaje de éxito al actualizar perfil
    LaunchedEffect(updateProfileState) {
        when (updateProfileState) {
            is UpdateProfileState.Success -> {
                showEditDialog = false
                viewModel.resetUpdateState()
            }
            else -> {}
        }
    }
    
    // Mostrar mensaje de éxito al cambiar contraseña
    LaunchedEffect(changePasswordState) {
        when (changePasswordState) {
            is ChangePasswordState.Success -> {
                showPasswordDialog = false
                viewModel.resetPasswordState()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = XantinaPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        when (val state = profileState) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = XantinaPrimary)
                }
            }
            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            scope.launch {
                                val token = authRepository.getAuthToken()
                                if (token != null) {
                                    viewModel.loadProfile(token)
                                }
                            }
                        }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            is ProfileUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Avatar y nombre
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(XantinaPrimary, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.user.name.firstOrNull()?.uppercase() ?: "U",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = state.user.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = state.user.email,
                            fontSize = 16.sp,
                            color = XantinaTextSecondary
                        )
                        
                        Text(
                            text = "Rol: ${state.user.role}",
                            fontSize = 14.sp,
                            color = XantinaTextSecondary
                        )
                    }
                    
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Opciones de perfil
                    ProfileOption(
                        icon = Icons.Default.Edit,
                        title = "Editar Perfil",
                        subtitle = "Actualiza tu nombre y email",
                        onClick = { showEditDialog = true }
                    )
                    
                    ProfileOption(
                        icon = Icons.Default.Lock,
                        title = "Cambiar Contraseña",
                        subtitle = "Actualiza tu contraseña",
                        onClick = { showPasswordDialog = true }
                    )
                }
            }
            ProfileUiState.Initial -> {
                // Estado inicial, no mostrar nada aún
            }
        }
    }
    
    // Diálogo para editar perfil
    if (showEditDialog && profileState is ProfileUiState.Success) {
        EditProfileDialog(
            currentName = (profileState as ProfileUiState.Success).user.name,
            currentEmail = (profileState as ProfileUiState.Success).user.email,
            updateState = updateProfileState,
            onDismiss = {
                showEditDialog = false
                viewModel.resetUpdateState()
            },
            onSave = { name, email ->
                scope.launch {
                    val token = authRepository.getAuthToken()
                    if (token != null) {
                        viewModel.updateProfile(token, name, email)
                    }
                }
            }
        )
    }
    
    // Diálogo para cambiar contraseña
    if (showPasswordDialog) {
        ChangePasswordDialog(
            passwordState = changePasswordState,
            onDismiss = {
                showPasswordDialog = false
                viewModel.resetPasswordState()
            },
            onSave = { currentPassword, newPassword ->
                scope.launch {
                    val token = authRepository.getAuthToken()
                    if (token != null) {
                        viewModel.changePassword(token, currentPassword, newPassword)
                    }
                }
            }
        )
    }
}

@Composable
fun ProfileOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = XantinaPrimary,
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = XantinaTextSecondary
            )
        }
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentEmail: String,
    updateState: UpdateProfileState,
    onDismiss: () -> Unit,
    onSave: (String?, String?) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var email by remember { mutableStateOf(currentEmail) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Perfil") },
        text = {
            Column {
                XantinaTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre",
                    placeholder = "Ingresa tu nombre",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                XantinaTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    placeholder = "Ingresa tu email",
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Mostrar error si hay
                if (updateState is UpdateProfileState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = updateState.message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            XantinaButton(
                text = if (updateState is UpdateProfileState.Loading) "Guardando..." else "Guardar",
                onClick = {
                    val updatedName = if (name != currentName) name else null
                    val updatedEmail = if (email != currentEmail) email else null
                    onSave(updatedName, updatedEmail)
                },
                enabled = updateState !is UpdateProfileState.Loading
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ChangePasswordDialog(
    passwordState: ChangePasswordState,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Contraseña") },
        text = {
            Column {
                XantinaTextField(
                    value = currentPassword,
                    onValueChange = { 
                        currentPassword = it
                        localError = null
                    },
                    label = "Contraseña Actual",
                    placeholder = "Ingresa tu contraseña actual",
                    modifier = Modifier.fillMaxWidth(),
                    isPassword = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                XantinaTextField(
                    value = newPassword,
                    onValueChange = { 
                        newPassword = it
                        localError = null
                    },
                    label = "Nueva Contraseña",
                    placeholder = "Mínimo 6 caracteres",
                    modifier = Modifier.fillMaxWidth(),
                    isPassword = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                XantinaTextField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it
                        localError = null
                    },
                    label = "Confirmar Contraseña",
                    placeholder = "Repite la nueva contraseña",
                    modifier = Modifier.fillMaxWidth(),
                    isPassword = true
                )
                
                // Mostrar error local o del servidor
                val errorMessage = localError ?: (passwordState as? ChangePasswordState.Error)?.message
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            XantinaButton(
                text = if (passwordState is ChangePasswordState.Loading) "Cambiando..." else "Cambiar",
                onClick = {
                    when {
                        currentPassword.isBlank() -> localError = "Ingresa tu contraseña actual"
                        newPassword.length < 6 -> localError = "La nueva contraseña debe tener al menos 6 caracteres"
                        newPassword != confirmPassword -> localError = "Las contraseñas no coinciden"
                        else -> {
                            localError = null
                            onSave(currentPassword, newPassword)
                        }
                    }
                },
                enabled = passwordState !is ChangePasswordState.Loading
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
