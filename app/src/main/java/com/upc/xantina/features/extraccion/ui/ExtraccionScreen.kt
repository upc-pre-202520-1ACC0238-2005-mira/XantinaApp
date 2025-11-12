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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.upc.xantina.shared.ui.components.MethodCard
import com.upc.xantina.shared.ui.components.RecentCard
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextSecondary

@Composable
fun ExtraccionScreen(
    userId: String?,
    onNavigateToCreate: (String?) -> Unit,
    onNavigateToAll: () -> Unit,
    onMethodClick: (MetodoExtraccion) -> Unit,
    onRecentClick: (String) -> Unit,
    viewModel: ExtraccionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val contexto = LocalContext.current

    LaunchedEffect(userId) {
        viewModel.cargarDatos(userId)
    }

    LaunchedEffect(uiState.successMessage) {
        val mensaje = uiState.successMessage ?: return@LaunchedEffect
        Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
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
                onClick = { onNavigateToCreate(userId) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = Color(0xFF4B2E2E),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp,
                    hoveredElevation = 10.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear nuevo método",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
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
    onMethodClick: (MetodoExtraccion) -> Unit
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
                    onClick = { onMethodClick(metodo) }
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
