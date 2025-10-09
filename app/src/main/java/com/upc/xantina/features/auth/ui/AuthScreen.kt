package com.upc.xantina.features.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.upc.xantina.shared.ui.components.AuthTab
import com.upc.xantina.shared.ui.components.BackgroundGradient
import com.upc.xantina.shared.ui.components.XantinaButton
import com.upc.xantina.shared.ui.components.XantinaLogo
import com.upc.xantina.shared.ui.components.XantinaTabSelector
import com.upc.xantina.shared.ui.components.XantinaTextField
import com.upc.xantina.shared.ui.theme.XantinaBackground
import com.upc.xantina.shared.ui.theme.XantinaCardBackground

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(AuthTab.LOGIN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
            
            // Logo y título - más prominente
            XantinaLogo(
                modifier = Modifier.padding(bottom = 48.dp)
            )
            
            // Selector de tabs con mejor diseño
            XantinaTabSelector(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    errorMessage = null
                    // Limpiar campos al cambiar tab
                    email = ""
                    password = ""
                    name = ""
                },
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Formulario con mejor espaciado
            when (selectedTab) {
                AuthTab.LOGIN -> {
                    LoginForm(
                        email = email,
                        onEmailChange = { email = it },
                        password = password,
                        onPasswordChange = { password = it },
                        onLoginClick = {
                            // TODO: Implementar lógica de login
                            isLoading = true
                            // Simular llamada
                            onLoginSuccess()
                        },
                        isLoading = isLoading
                    )
                }
                AuthTab.REGISTER -> {
                    RegisterForm(
                        name = name,
                        onNameChange = { name = it },
                        email = email,
                        onEmailChange = { email = it },
                        password = password,
                        onPasswordChange = { password = it },
                        onRegisterClick = {
                            // TODO: Implementar lógica de registro
                            isLoading = true
                            // Simular llamada
                            onRegisterSuccess()
                        },
                        isLoading = isLoading
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
    isLoading: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        XantinaTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email",
            placeholder = "tu@email.com",
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
        )
        
        XantinaTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Contraseña",
            placeholder = "........",
            isPassword = true
        )
        
        XantinaButton(
            text = "Iniciar Sesión",
            onClick = onLoginClick,
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
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
    isLoading: Boolean
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
            label = "Email",
            placeholder = "tu@email.com",
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
        )
        
        XantinaTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Contraseña",
            placeholder = "........",
            isPassword = true
        )
        
        XantinaButton(
            text = "Crear Cuenta",
            onClick = onRegisterClick,
            enabled = !isLoading && name.isNotBlank() && email.isNotBlank() && password.isNotBlank()
        )
    }
}
