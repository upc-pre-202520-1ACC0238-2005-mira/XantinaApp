@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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

    val uiStateMetodos = viewModel.uiState.collectAsState().value.metodos
    
    var nombre by rememberSaveable { mutableStateOf("") }
    var metodoSeleccionado by rememberSaveable { mutableStateOf("") }
    var etiqueta by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var ratio by rememberSaveable { mutableStateOf("1:15") }
    var gramosCafe by rememberSaveable { mutableStateOf("15.0") }
    var mililitrosAgua by rememberSaveable { mutableStateOf("225.0") }
    var temperaturaAgua by rememberSaveable { mutableStateOf("") }
    var tiempoExtraccion by rememberSaveable { mutableStateOf("") }
    var calificacion by rememberSaveable { mutableStateOf("5") }
    var ratioBloqueado by rememberSaveable { mutableStateOf(true) }
    var metodoMenuExpanded by remember { mutableStateOf(false) }

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
    
    // Cargar métodos disponibles al abrir la pantalla
    LaunchedEffect(Unit) {
        if (userId != null) {
            viewModel.cargarDatos(userId)
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
            metodoSeleccionado = metodoSeleccionado,
            onMetodoSeleccionadoChange = { metodoSeleccionado = it },
            metodosDisponibles = uiStateMetodos.map { it.nombre },
            metodoMenuExpanded = metodoMenuExpanded,
            onMetodoMenuExpandedChange = { metodoMenuExpanded = it },
            etiqueta = etiqueta,
            onEtiquetaChange = { etiqueta = it },
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
            ratioBloqueado = ratioBloqueado,
            onRatioBloqueadoChange = { ratioBloqueado = it },
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
                    metodo = metodoSeleccionado.trim().ifBlank { nombre.trim() },
                    etiqueta = etiqueta.trim().ifBlank { null },
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
    metodoSeleccionado: String,
    onMetodoSeleccionadoChange: (String) -> Unit,
    metodosDisponibles: List<String>,
    metodoMenuExpanded: Boolean,
    onMetodoMenuExpandedChange: (Boolean) -> Unit,
    etiqueta: String,
    onEtiquetaChange: (String) -> Unit,
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
    ratioBloqueado: Boolean,
    onRatioBloqueadoChange: (Boolean) -> Unit,
    isSaving: Boolean,
    isValid: Boolean,
    onSubmit: () -> Unit
) {
    // Extraer el ratio actual del string
    val ratioActual = remember(ratio) {
        ratio.substringAfter(":").toDoubleOrNull() ?: 15.0
    }
    
    // Funciones para cálculo automático del ratio
    fun actualizarAguaDesdeCafe(nuevoCafe: String) {
        val cafe = nuevoCafe.toDoubleOrNull() ?: return
        onGramosCafeChange(nuevoCafe)
        
        if (ratioBloqueado && cafe > 0) {
            val nuevaAgua = cafe * ratioActual
            onMililitrosAguaChange(String.format("%.1f", nuevaAgua))
        } else if (!ratioBloqueado && cafe > 0) {
            // Calcular nuevo ratio
            val agua = mililitrosAgua.toDoubleOrNull() ?: 0.0
            if (agua > 0) {
                val nuevoRatio = agua / cafe
                onRatioChange("1:${String.format("%.1f", nuevoRatio)}")
            }
        }
    }
    
    fun actualizarCafeDesdeAgua(nuevaAgua: String) {
        val agua = nuevaAgua.toDoubleOrNull() ?: return
        onMililitrosAguaChange(nuevaAgua)
        
        if (ratioBloqueado && agua > 0 && ratioActual > 0) {
            val nuevoCafe = agua / ratioActual
            onGramosCafeChange(String.format("%.1f", nuevoCafe))
        } else if (!ratioBloqueado && agua > 0) {
            // Calcular nuevo ratio
            val cafe = gramosCafe.toDoubleOrNull() ?: 0.0
            if (cafe > 0) {
                val nuevoRatio = agua / cafe
                onRatioChange("1:${String.format("%.1f", nuevoRatio)}")
            }
        }
    }
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
                label = "Nombre de receta",
                placeholder = "Ej: Mi Aeropress Favorito",
                isRequired = true
            )

            // Menú desplegable para Método
            Column {
                Text(
                    text = "Método de la receta *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2C1810),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = metodoMenuExpanded,
                    onExpandedChange = onMetodoMenuExpandedChange
                ) {
                    OutlinedTextField(
                        value = metodoSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { 
                            Text(
                                text = "Selecciona un método",
                                color = Color(0xFFAAAAAA),
                                fontSize = 15.sp
                            ) 
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = metodoMenuExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4B2E2E),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color(0xFFFAFAFA),
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 15.sp,
                            color = Color(0xFF2C1810)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = metodoMenuExpanded,
                        onDismissRequest = { onMetodoMenuExpandedChange(false) }
                    ) {
                        metodosDisponibles.forEach { metodo ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(metodo) },
                                onClick = {
                                    onMetodoSeleccionadoChange(metodo)
                                    onMetodoMenuExpandedChange(false)
                                }
                            )
                        }
                    }
                }
            }

            StyledTextField(
                value = etiqueta,
                onValueChange = onEtiquetaChange,
                label = "Etiqueta",
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
            // Card de ratio con bloqueo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (ratioBloqueado) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (ratioBloqueado) "Ratio bloqueado" else "Ratio calculado",
                            fontSize = 13.sp,
                            color = if (ratioBloqueado) Color(0xFFC62828) else Color(0xFF2E7D32),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            ratio,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ratioBloqueado) Color(0xFFD32F2F) else Color(0xFF388E3C)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (ratioBloqueado) "Los valores se calcularán automáticamente" else "El ratio se ajusta según tus valores",
                            fontSize = 11.sp,
                            color = Color(0xFF666666)
                        )
                    }
                    Button(
                        onClick = { onRatioBloqueadoChange(!ratioBloqueado) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (ratioBloqueado) Color(0xFFD32F2F) else Color(0xFF388E3C)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = if (ratioBloqueado) "Bloqueado" else "Desbloqueado",
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (ratioBloqueado) "Bloqueado" else "Libre",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StyledTextField(
                    value = gramosCafe,
                    onValueChange = { actualizarAguaDesdeCafe(it) },
                    label = "Gramos de café",
                    placeholder = "15",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )

                StyledTextField(
                    value = mililitrosAgua,
                    onValueChange = { actualizarCafeDesdeAgua(it) },
                    label = "Mililitros de agua",
                    placeholder = "250",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )
            }

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
            
            StyledTextField(
                value = calificacion,
                onValueChange = onCalificacionChange,
                label = "Calificación",
                placeholder = "1-5",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )
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

