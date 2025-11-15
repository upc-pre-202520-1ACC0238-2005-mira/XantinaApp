package com.upc.xantina.features.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.upc.xantina.shared.ui.components.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.upc.xantina.features.auth.presentation.state.AuthAction
import com.upc.xantina.features.auth.presentation.viewmodel.AuthViewModel
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(AuthTab.LOGIN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var localErrorMessage by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState.isLoading
    val errorMessage = localErrorMessage ?: uiState.errorMessage

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun clearInputs() {
        selectedTab = AuthTab.LOGIN
        email = ""
        password = ""
        name = ""
        localErrorMessage = null
        viewModel.consumeAuthAction()
    }

    LaunchedEffect(uiState.isAuthenticated, uiState.lastAction) {
        if (uiState.isAuthenticated) {
            when (uiState.lastAction) {
                AuthAction.LOGIN -> {
                    onLoginSuccess()
                    clearInputs()
                }
                AuthAction.REGISTER -> {
                    onRegisterSuccess()
                    clearInputs()
                }
                else -> Unit
            }
        }
    }

    fun resetErrors() {
        localErrorMessage = null
        viewModel.clearError()
    }

    BackgroundGradient {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            XantinaLogo(
                modifier = Modifier.padding(bottom = 48.dp)
            )

            XantinaTabSelector(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    resetErrors()
                    email = ""
                    password = ""
                    name = ""
                },
                modifier = Modifier.padding(bottom = 32.dp)
            )

            when (selectedTab) {
                AuthTab.LOGIN -> {
                    LoginForm(
                        email = email,
                        onEmailChange = {
                            email = it
                            resetErrors()
                        },
                        password = password,
                        onPasswordChange = {
                            password = it
                            resetErrors()
                        },
                        onLoginClick = {
                            when {
                                email.isBlank() || password.isBlank() -> {
                                    localErrorMessage = "Por favor ingresa tu correo y contraseña."
                                }
                                !isValidEmail(email) -> {
                                    localErrorMessage = "El correo ingresado no tiene un formato válido."
                                }
                                else -> {
                                    resetErrors()
                                    viewModel.login(email.trim(), password)
                                }
                            }
                        },
                        isLoading = isLoading,
                        errorMessage = errorMessage
                    )
                }

                AuthTab.REGISTER -> {
                    RegisterForm(
                        name = name,
                        onNameChange = {
                            name = it
                            resetErrors()
                        },
                        email = email,
                        onEmailChange = {
                            email = it
                            resetErrors()
                        },
                        password = password,
                        onPasswordChange = {
                            password = it
                            resetErrors()
                        },
                        onRegisterClick = {
                            when {
                                name.isBlank() || email.isBlank() || password.isBlank() -> {
                                    localErrorMessage = "Completa todos los campos antes de registrarte."
                                }
                                !isValidEmail(email) -> {
                                    localErrorMessage = "El correo ingresado no tiene un formato válido."
                                }
                                else -> {
                                    resetErrors()
                                    viewModel.register(
                                        name = name.trim(),
                                        email = email.trim(),
                                        password = password
                                    )
                                }
                            }
                        },
                        isLoading = isLoading,
                        errorMessage = errorMessage
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun LoginForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        XantinaTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Correo electrónico",
            placeholder = "tu@email.com",
            keyboardType = KeyboardType.Email
        )

        XantinaTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Contraseña",
            placeholder = "........",
            isPassword = true
        )

        if (!errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        XantinaButton(
            text = "Iniciar Sesión",
            onClick = onLoginClick,
            enabled = !isLoading
        )
    }
}

@Composable
private fun RegisterForm(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        XantinaTextField(
            value = name,
            onValueChange = onNameChange,
            label = "Nombre",
            placeholder = "Tu nombre"
        )

        XantinaTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Correo electrónico",
            placeholder = "tu@email.com",
            keyboardType = KeyboardType.Email
        )

        XantinaTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Contraseña",
            placeholder = "........",
            isPassword = true
        )

        if (!errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        XantinaButton(
            text = "Crear Cuenta",
            onClick = onRegisterClick,
            enabled = !isLoading
        )
    }
}
