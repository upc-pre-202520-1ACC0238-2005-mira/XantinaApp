package com.upc.xantina.features.extraccion.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.shared.ui.components.BackgroundGradient
import com.upc.xantina.shared.ui.components.BottomNavTab
import com.upc.xantina.shared.ui.components.XantinaBottomNavigation
import com.upc.xantina.shared.ui.components.MethodCard
import com.upc.xantina.shared.ui.components.RecentCard
import com.upc.xantina.shared.ui.components.XantinaButton
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextSecondary

@Composable
fun ExtraccionScreen(
    onNavigateToCreate: () -> Unit = {},
    onNavigateToAll: () -> Unit = {},
    onMethodClick: (String) -> Unit = {},
    onRecentClick: (String) -> Unit = {}
) {
    // Datos mock para la demo
    var metodos by remember { mutableStateOf(getMockMetodos()) }
    var extraccionesRecientes by remember { mutableStateOf(getMockExtraccionesRecientes()) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header fijo
        HeaderSection()
        
        // Contenido scrolleable
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp) // Espacio para el FAB
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Sección Métodos
                MetodosSection(
                    metodos = metodos,
                    onMethodClick = onMethodClick
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Sección Recientes
                RecientesSection(
                    extraccionesRecientes = extraccionesRecientes,
                    onRecentClick = onRecentClick,
                    onNavigateToAll = onNavigateToAll
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Floating Action Button
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
        Spacer(modifier = Modifier.height(16.dp))
        
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
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
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
            
            androidx.compose.material3.TextButton(
                onClick = onNavigateToAll
            ) {
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

// Datos mock para la demo
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

private fun getMockMetodos(): List<MetodoExtraccionMock> {
    return listOf(
        MetodoExtraccionMock(
            nombre = "Prensa Francesa",
            descripcion = "Cuerpo completo y sabores intensos",
            tiempoPreparacion = "4 min",
            icono = "prensa"
        ),
        MetodoExtraccionMock(
            nombre = "V60",
            descripcion = "Notas brillantes y claridad",
            tiempoPreparacion = "2-3 min",
            icono = "v60"
        ),
        MetodoExtraccionMock(
            nombre = "Aeropress",
            descripcion = "Extracción rápida y consistente",
            tiempoPreparacion = "1-2 min",
            icono = "aeropress"
        ),
        MetodoExtraccionMock(
            nombre = "Chemex",
            descripcion = "Café limpio y elegante",
            tiempoPreparacion = "4-5 min",
            icono = "chemex"
        ),
        MetodoExtraccionMock(
            nombre = "Espresso",
            descripcion = "Intenso y concentrado",
            tiempoPreparacion = "30 seg",
            icono = "espresso"
        )
    )
}

private fun getMockExtraccionesRecientes(): List<ExtraccionRecienteMock> {
    return listOf(
        ExtraccionRecienteMock(
            id = "1",
            nombreCafe = "Colombia Geisha",
            metodoExtraccion = "V60",
            fechaHora = "Hoy, 8:30 AM",
            calificacion = 5
        ),
        ExtraccionRecienteMock(
            id = "2",
            nombreCafe = "Brasil Natural",
            metodoExtraccion = "Prensa Francesa",
            fechaHora = "Ayer, 10:15 AM",
            calificacion = 4
        ),
        ExtraccionRecienteMock(
            id = "3",
            nombreCafe = "Etiopía Yirgacheffe",
            metodoExtraccion = "Chemex",
            fechaHora = "Ayer, 2:30 PM",
            calificacion = 5
        ),
        ExtraccionRecienteMock(
            id = "4",
            nombreCafe = "Guatemala Huehuetenango",
            metodoExtraccion = "Aeropress",
            fechaHora = "Hace 2 días, 9:45 AM",
            calificacion = 4
        ),
        ExtraccionRecienteMock(
            id = "5",
            nombreCafe = "Kenya AA",
            metodoExtraccion = "V60",
            fechaHora = "Hace 3 días, 11:20 AM",
            calificacion = 5
        ),
        ExtraccionRecienteMock(
            id = "6",
            nombreCafe = "Costa Rica Tarrazú",
            metodoExtraccion = "Espresso",
            fechaHora = "Hace 3 días, 4:15 PM",
            calificacion = 3
        ),
        ExtraccionRecienteMock(
            id = "7",
            nombreCafe = "Perú Organic",
            metodoExtraccion = "Prensa Francesa",
            fechaHora = "Hace 4 días, 7:30 AM",
            calificacion = 4
        )
    )
}
