package com.upc.xantina.features.extraccion.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.upc.xantina.features.extraccion.presentation.viewmodel.ExtraccionViewModel
import com.upc.xantina.features.extraccion.presentation.viewmodel.MetodoCreacionDatos
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearMetodoScreen(
    userId: String?,
    onBack: () -> Unit,
    onCreated: () -> Unit,
    viewModel: ExtraccionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var nombre by rememberSaveable { mutableStateOf("") }
    var metodo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var ratio by rememberSaveable { mutableStateOf("1:15") }
    var gramosCafe by rememberSaveable { mutableStateOf("") }
    var mililitrosAgua by rememberSaveable { mutableStateOf("") }
    var temperaturaAgua by rememberSaveable { mutableStateOf("") }
    var tiempoExtraccion by rememberSaveable { mutableStateOf("") }
    var calificacion by rememberSaveable { mutableStateOf("5") }

    BackHandler(enabled = true) {
        onBack()
    }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
    }

    LaunchedEffect(uiState.successMessage) {
        val message = uiState.successMessage ?: return@LaunchedEffect
        if (message.isNotBlank()) {
            onCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Nuevo método", 
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4B2E2E)
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        CrearMetodoForm(
            padding = padding,
            nombre = nombre,
            onNombreChange = { nombre = it },
            metodo = metodo,
            onMetodoChange = { metodo = it },
            descripcion = descripcion,
            onDescripcionChange = { descripcion = it },
            ratio = ratio,
            onRatioChange = { ratio = it },
            gramosCafe = gramosCafe,
            onGramosCafeChange = { gramosCafe = it },
            mililitrosAgua = mililitrosAgua,
            onMililitrosAguaChange = { mililitrosAgua = it },
            temperaturaAgua = temperaturaAgua,
            onTemperaturaAguaChange = { temperaturaAgua = it },
            tiempoExtraccion = tiempoExtraccion,
            onTiempoExtraccionChange = { tiempoExtraccion = it },
            calificacion = calificacion,
            onCalificacionChange = { calificacion = it },
            isSaving = uiState.isSaving,
            isValid = nombre.isNotBlank() && userId != null,
            onSubmit = {
                if (userId.isNullOrBlank()) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Debes iniciar sesión para crear un método.")
                    }
                    return@CrearMetodoForm
                }
                val datos = MetodoCreacionDatos(
                    nombre = nombre.trim(),
                    metodo = metodo.trim().ifBlank { nombre.trim() },
                    ratio = ratio.trim().ifBlank { "1:15" },
                    descripcion = descripcion.trim().ifBlank { null },
                    gramosCafe = gramosCafe.toDoubleOrNull(),
                    mililitrosAgua = mililitrosAgua.toDoubleOrNull(),
                    temperaturaAgua = temperaturaAgua.toIntOrNull(),
                    tiempoExtraccion = tiempoExtraccion.toIntOrNull(),
                    calificacion = calificacion.toIntOrNull()
                )
                viewModel.crearMetodo(userId, datos)
            }
        )
    }
}

@Composable
private fun CrearMetodoForm(
    padding: PaddingValues,
    nombre: String,
    onNombreChange: (String) -> Unit,
    metodo: String,
    onMetodoChange: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    ratio: String,
    onRatioChange: (String) -> Unit,
    gramosCafe: String,
    onGramosCafeChange: (String) -> Unit,
    mililitrosAgua: String,
    onMililitrosAguaChange: (String) -> Unit,
    temperaturaAgua: String,
    onTemperaturaAguaChange: (String) -> Unit,
    tiempoExtraccion: String,
    onTiempoExtraccionChange: (String) -> Unit,
    calificacion: String,
    onCalificacionChange: (String) -> Unit,
    isSaving: Boolean,
    isValid: Boolean,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header con descripción
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Define tu receta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C1810)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Personaliza tu método de extracción para guardarlo y reutilizarlo.",
                    fontSize = 15.sp,
                    color = Color(0xFF666666),
                    lineHeight = 22.sp
                )
            }
        }

        // Sección: Información Básica
        FormSection(
            title = "Información Básica",
            icon = Icons.Filled.Edit
        ) {
            StyledTextField(
                value = nombre,
                onValueChange = onNombreChange,
                label = "Nombre del método",
                placeholder = "Ej: Mi Aeropress Favorito",
                isRequired = true
            )

            StyledTextField(
                value = metodo,
                onValueChange = onMetodoChange,
                label = "Etiqueta o familia",
                placeholder = "Ej: Aeropress, V60, French Press"
            )

            StyledTextField(
                value = descripcion,
                onValueChange = onDescripcionChange,
                label = "Descripción",
                placeholder = "Describe tu método de extracción...",
                minLines = 3
            )
        }

        // Sección: Parámetros de Extracción
        FormSection(
            title = "Parámetros de Extracción",
            icon = Icons.Filled.Settings
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StyledTextField(
                    value = gramosCafe,
                    onValueChange = onGramosCafeChange,
                    label = "Gramos de café",
                    placeholder = "15",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )

                StyledTextField(
                    value = mililitrosAgua,
                    onValueChange = onMililitrosAguaChange,
                    label = "Mililitros de agua",
                    placeholder = "250",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }

            StyledTextField(
                value = ratio,
                onValueChange = onRatioChange,
                label = "Ratio (café:agua)",
                placeholder = "1:15"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StyledTextField(
                    value = temperaturaAgua,
                    onValueChange = onTemperaturaAguaChange,
                    label = "Temperatura (°C)",
                    placeholder = "92",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )

                StyledTextField(
                    value = tiempoExtraccion,
                    onValueChange = onTiempoExtraccionChange,
                    label = "Tiempo (seg)",
                    placeholder = "120",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Botón de guardar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(24.dp)
        ) {
            Button(
                onClick = onSubmit,
                enabled = isValid && !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4B2E2E),
                    disabledContainerColor = Color(0xFF4B2E2E).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guardar método",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FormSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF4B2E2E),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C1810)
                )
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    icon: ImageVector? = null
) {
    Column(modifier = modifier) {
        Text(
            text = if (isRequired) "$label *" else label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2C1810),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { 
                Text(
                    text = placeholder,
                    color = Color(0xFFAAAAAA),
                    fontSize = 15.sp
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4B2E2E),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color(0xFFFAFAFA),
                unfocusedContainerColor = Color(0xFFFAFAFA)
            ),
            shape = RoundedCornerShape(12.dp),
            minLines = minLines,
            maxLines = if (minLines > 1) 5 else 1,
            singleLine = minLines == 1,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            leadingIcon = icon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = Color(0xFF4B2E2E).copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp,
                color = Color(0xFF2C1810)
            )
        )
    }
}

