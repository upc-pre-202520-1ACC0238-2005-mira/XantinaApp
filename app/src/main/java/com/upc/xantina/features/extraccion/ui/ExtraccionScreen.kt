package com.upc.xantina.features.extraccion.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.shared.ui.components.MethodCard
import com.upc.xantina.shared.ui.components.RecentCard
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextSecondary

@Composable
fun ExtraccionScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToAll: () -> Unit,
    onMethodClick: (String) -> Unit,
    onRecentClick: (String) -> Unit
) {
    val metodos = getMockMetodos()
    val extraccionesRecientes = getMockExtraccionesRecientes()

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderSection()

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 16.dp, bottom = 100.dp)
            ) {
                MetodosSection(metodos = metodos, onMethodClick = onMethodClick)

                Spacer(modifier = Modifier.height(24.dp))

                RecientesSection(
                    extraccionesRecientes = extraccionesRecientes,
                    onRecentClick = onRecentClick,
                    onNavigateToAll = onNavigateToAll
                )
            }

            FloatingActionButton(
                onClick = onNavigateToCreate,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = XantinaPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear nueva extracción",
                    tint = androidx.compose.ui.graphics.Color.White
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
            color = androidx.compose.ui.graphics.Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Crea tu próxima taza perfecta",
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun MetodosSection(
    metodos: List<MetodoExtraccionMock>,
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

        metodos.forEach { metodo ->
            MethodCard(
                nombre = metodo.nombre,
                descripcion = metodo.descripcion,
                tiempoPreparacion = metodo.tiempoPreparacion,
                icono = metodo.icono,
                onClick = { onMethodClick(metodo.nombre) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun RecientesSection(
    extraccionesRecientes: List<ExtraccionRecienteMock>,
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

        extraccionesRecientes.forEach { extraccion ->
            RecentCard(
                nombreCafe = extraccion.nombreCafe,
                metodoExtraccion = extraccion.metodoExtraccion,
                fechaHora = extraccion.fechaHora,
                calificacion = extraccion.calificacion,
                onClick = { onRecentClick(extraccion.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// ---------------- Mock Data ----------------

data class MetodoExtraccionMock(
    val nombre: String,
    val descripcion: String,
    val tiempoPreparacion: String,
    val icono: String
)

data class ExtraccionRecienteMock(
    val id: String,
    val nombreCafe: String,
    val metodoExtraccion: String,
    val fechaHora: String,
    val calificacion: Int
)

private fun getMockMetodos(): List<MetodoExtraccionMock> = listOf(
    MetodoExtraccionMock("Prensa Francesa", "Cuerpo completo y sabores intensos", "4 min", "prensa"),
    MetodoExtraccionMock("V60", "Notas brillantes y claridad", "2-3 min", "v60"),
    MetodoExtraccionMock("Aeropress", "Extracción rápida y consistente", "1-2 min", "aeropress"),
    MetodoExtraccionMock("Chemex", "Café limpio y elegante", "4-5 min", "chemex"),
    MetodoExtraccionMock("Espresso", "Intenso y concentrado", "30 seg", "espresso")
)

private fun getMockExtraccionesRecientes(): List<ExtraccionRecienteMock> = listOf(
    ExtraccionRecienteMock("1", "Colombia Geisha", "V60", "Hoy, 8:30 AM", 5),
    ExtraccionRecienteMock("2", "Brasil Natural", "Prensa Francesa", "Ayer, 10:15 AM", 4),
    ExtraccionRecienteMock("3", "Etiopía Yirgacheffe", "Chemex", "Ayer, 2:30 PM", 5),
    ExtraccionRecienteMock("4", "Guatemala Huehuetenango", "Aeropress", "Hace 2 días, 9:45 AM", 4),
    ExtraccionRecienteMock("5", "Kenya AA", "V60", "Hace 3 días, 11:20 AM", 5),
    ExtraccionRecienteMock("6", "Costa Rica Tarrazú", "Espresso", "Hace 3 días, 4:15 PM", 3),
    ExtraccionRecienteMock("7", "Perú Organic", "Prensa Francesa", "Hace 4 días, 7:30 AM", 4)
)
