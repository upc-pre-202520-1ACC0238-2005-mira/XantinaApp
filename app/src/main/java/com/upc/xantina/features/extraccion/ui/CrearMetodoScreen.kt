package com.upc.xantina.features.extraccion.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
                title = { Text(text = "Nuevo método", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Define tu receta",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Personaliza tu método de extracción para guardarlo y reutilizarlo.",
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = onNombreChange,
            label = { Text("Nombre del método (*)") },
            singleLine = true
        )

        OutlinedTextField(
            value = metodo,
            onValueChange = onMetodoChange,
            label = { Text("Etiqueta o familia") },
            singleLine = true
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = onDescripcionChange,
            label = { Text("Descripción") },
            minLines = 3
        )

        OutlinedTextField(
            value = ratio,
            onValueChange = onRatioChange,
            label = { Text("Ratio (ej. 1:15)") },
            singleLine = true
        )

        OutlinedTextField(
            value = gramosCafe,
            onValueChange = onGramosCafeChange,
            label = { Text("Gramos de café") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        OutlinedTextField(
            value = mililitrosAgua,
            onValueChange = onMililitrosAguaChange,
            label = { Text("Mililitros de agua") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        OutlinedTextField(
            value = temperaturaAgua,
            onValueChange = onTemperaturaAguaChange,
            label = { Text("Temperatura (°C)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        OutlinedTextField(
            value = tiempoExtraccion,
            onValueChange = onTiempoExtraccionChange,
            label = { Text("Tiempo (seg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        OutlinedTextField(
            value = calificacion,
            onValueChange = onCalificacionChange,
            label = { Text("Calificación (1-5)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Button(
            onClick = onSubmit,
            enabled = isValid && !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(20.dp)
                )
            } else {
                Text(text = "Guardar método")
            }
        }
    }
}

