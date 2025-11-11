package com.upc.xantina.features.extraccion.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.upc.xantina.features.extraccion.domain.model.Extraccion
import com.upc.xantina.features.extraccion.domain.model.MetodoExtraccion
import com.upc.xantina.features.extraccion.presentation.state.MetodoFiltro
import com.upc.xantina.features.extraccion.presentation.viewmodel.ExtraccionViewModel
import com.upc.xantina.features.extraccion.presentation.viewmodel.MetodoCreacionDatos
import com.upc.xantina.shared.ui.components.MethodCard
import com.upc.xantina.shared.ui.components.RecentCard
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextSecondary

@Composable
fun ExtraccionScreen(
    userId: String?,
    onNavigateToCreate: () -> Unit, // Reservado para futuras integraciones
    onNavigateToAll: () -> Unit,
    onMethodClick: (String) -> Unit,
    onRecentClick: (String) -> Unit,
    viewModel: ExtraccionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarDialogoCreacion by remember { mutableStateOf(false) }
    val contexto = LocalContext.current

    LaunchedEffect(userId) {
        viewModel.cargarDatos(userId)
    }

    LaunchedEffect(uiState.successMessage) {
        val mensaje = uiState.successMessage ?: return@LaunchedEffect
        Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
        mostrarDialogoCreacion = false
        viewModel.consumirMensajes()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderSection()

        MetodoFiltroRow(
            filtroActual = uiState.selectedFiltro,
            onFiltroSeleccionado = viewModel::seleccionarFiltro
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(42.dp),
                    color = XantinaPrimary
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 16.dp, bottom = 100.dp)
                ) {
                    MetodosSection(
                        metodos = uiState.metodos,
                        onMethodClick = onMethodClick
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    RecientesSection(
                        extraccionesRecientes = uiState.extraccionesRecientes,
                        onRecentClick = onRecentClick,
                        onNavigateToAll = onNavigateToAll
                    )

                    uiState.errorMessage?.let { mensaje ->
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = mensaje,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = { mostrarDialogoCreacion = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = XantinaPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear nuevo método",
                    tint = Color.White
                )
            }
        }
    }

    if (mostrarDialogoCreacion) {
        CrearMetodoDialog(
            isSaving = uiState.isSaving,
            onDismiss = {
                if (!uiState.isSaving) {
                    mostrarDialogoCreacion = false
                }
            },
            onGuardar = { datos ->
                viewModel.crearMetodo(userId, datos)
            }
        )
    }
}

@Composable
private fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(XantinaPrimary)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Extrae",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Crea tu próxima taza perfecta",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun MetodoFiltroRow(
    filtroActual: MetodoFiltro,
    onFiltroSeleccionado: (MetodoFiltro) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            MetodoFiltro.TODOS to "Todos",
            MetodoFiltro.MIS_METODOS to "Mis métodos"
        ).forEach { (filtro, etiqueta) ->
            FilterChip(
                selected = filtroActual == filtro,
                onClick = { onFiltroSeleccionado(filtro) },
                label = { Text(etiqueta) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = XantinaPrimary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun MetodosSection(
    metodos: List<MetodoExtraccion>,
    onMethodClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Métodos",
            color = XantinaTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (metodos.isEmpty()) {
            Text(
                text = "Aún no tienes métodos disponibles.",
                color = XantinaTextSecondary,
                fontSize = 14.sp
            )
        } else {
            metodos.forEach { metodo ->
                MethodCard(
                    nombre = metodo.nombre,
                    descripcion = metodo.descripcion,
                    tiempoPreparacion = metodo.tiempoPreparacion,
                    icono = metodo.icono,
                    onClick = { onMethodClick(metodo.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun RecientesSection(
    extraccionesRecientes: List<Extraccion>,
    onRecentClick: (String) -> Unit,
    onNavigateToAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recientes",
                color = XantinaTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onNavigateToAll) {
                Text(
                    text = "Ver todas",
                    color = XantinaTextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (extraccionesRecientes.isEmpty()) {
            Text(
                text = "Aún no registras extracciones.",
                color = XantinaTextSecondary,
                fontSize = 14.sp
            )
        } else {
            extraccionesRecientes.forEach { extraccion ->
                RecentCard(
                    nombreCafe = extraccion.nombreCafe,
                    metodoExtraccion = extraccion.metodoExtraccion,
                    fechaHora = extraccion.getFechaHoraCompleta(),
                    calificacion = extraccion.calificacion,
                    onClick = {
                        extraccion.id?.let(onRecentClick)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun CrearMetodoDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onGuardar: (MetodoCreacionDatos) -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var metodo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var ratio by rememberSaveable { mutableStateOf("1:15") }
    var gramosCafe by rememberSaveable { mutableStateOf("") }
    var mililitrosAgua by rememberSaveable { mutableStateOf("") }
    var temperaturaAgua by rememberSaveable { mutableStateOf("") }
    var tiempoExtraccion by rememberSaveable { mutableStateOf("") }
    var calificacion by rememberSaveable { mutableStateOf("5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
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
                    onGuardar(datos)
                },
                enabled = !isSaving && nombre.isNotBlank()
            ) {
                Text(text = if (isSaving) "Guardando..." else "Guardar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text("Cancelar")
            }
        },
        title = { Text(text = "Nuevo método de extracción") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del método") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = metodo,
                    onValueChange = { metodo = it },
                    label = { Text("Etiqueta o familia") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") }
                )
                OutlinedTextField(
                    value = ratio,
                    onValueChange = { ratio = it },
                    label = { Text("Ratio (ej. 1:15)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = gramosCafe,
                    onValueChange = { gramosCafe = it },
                    label = { Text("Gramos de café") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = mililitrosAgua,
                    onValueChange = { mililitrosAgua = it },
                    label = { Text("Mililitros de agua") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = temperaturaAgua,
                    onValueChange = { temperaturaAgua = it },
                    label = { Text("Temperatura (°C)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = tiempoExtraccion,
                    onValueChange = { tiempoExtraccion = it },
                    label = { Text("Tiempo (seg)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = calificacion,
                    onValueChange = { calificacion = it },
                    label = { Text("Calificación (1-5)") },
                    singleLine = true
                )
            }
        }
    )
}