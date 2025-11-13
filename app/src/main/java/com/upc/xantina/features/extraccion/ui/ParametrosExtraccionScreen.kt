package com.upc.xantina.features.extraccion.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.features.extraccion.domain.model.BolsaCafe
import com.upc.xantina.features.extraccion.domain.model.MetodoExtraccion
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import com.upc.xantina.shared.ui.theme.XantinaSecondary
import kotlin.math.roundToInt

@Composable
fun ParametrosExtraccionScreen(
    metodo: MetodoExtraccion,
    bolsasCafe: List<BolsaCafe>,
    isLoadingBolsas: Boolean,
    onRefreshBolsas: () -> Unit,
    onStart: (bolsaId: String, cantidadCafe: Int) -> Unit,
    onBack: () -> Unit
) {
    var cafeSeleccionado by remember { mutableStateOf<BolsaCafe?>(null) }
    var mostrarLista by remember { mutableStateOf(false) }

    val ratioRecomendado = remember(metodo.ratio) {
        metodo.ratio
            ?.substringAfter(":")
            ?.toDoubleOrNull()
            ?.toInt()
            ?.coerceAtLeast(1) ?: 15
    }

    var cantidadCafe by remember { mutableStateOf(15) }
    var cantidadAgua by remember { mutableStateOf(15 * ratioRecomendado) }
    var ratioBloqueado by remember { mutableStateOf(true) }

    val tiempoEstimadoMinutos = 2.5

    // Recalcular agua basado en ratio
    LaunchedEffect(cantidadCafe, ratioRecomendado, ratioBloqueado) {
        if (ratioBloqueado) {
            cantidadAgua = cantidadCafe * ratioRecomendado
        }
    }

    LaunchedEffect(bolsasCafe) {
        if (cafeSeleccionado == null && bolsasCafe.isNotEmpty()) {
            cafeSeleccionado = bolsasCafe.first()
        } else if (cafeSeleccionado != null && bolsasCafe.none { it.id == cafeSeleccionado?.id }) {
            cafeSeleccionado = bolsasCafe.firstOrNull()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {

        // Botón regresar mejorado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBack() }
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Regresar",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Regresar",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Título
        Text(
            "Configuración de extracción",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Ajusta los parámetros para tu preparación",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(20.dp))

        // Card con información del método
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Método: ${metodo.nombre}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    metodo.descripcion,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        // Card de ratio
        val ratioTexto = metodo.ratio ?: "1:$ratioRecomendado"
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Ratio sugerido",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        ratioTexto,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                OutlinedButton(
                    onClick = { ratioBloqueado = !ratioBloqueado },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = if (ratioBloqueado) "Bloqueado" else "Editable",
                        modifier = Modifier.size(16.dp),
                        tint = if (ratioBloqueado) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (ratioBloqueado) "Bloqueado" else "Editable",
                        fontSize = 12.sp
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))

        // ---- Selección de café ----
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Selecciona tu café",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onRefreshBolsas) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Actualizar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        if (isLoadingBolsas) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterHorizontally),
                strokeWidth = 3.dp,
                color = XantinaPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        } else if (bolsasCafe.isEmpty()) {
            Text(
                text = "Registra tus bolsas de café para poder seleccionarlas.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.outline
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarLista = !mostrarLista }
            ) {
                OutlinedTextField(
                    value = cafeSeleccionado?.let {
                        val restante = it.pesoRestante
                        "${it.nombre} · ${restante.roundToInt()} g restantes"
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    placeholder = { Text("Elegir café...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (mostrarLista) {
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(bolsasCafe) { bolsa ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        cafeSeleccionado = bolsa
                                        mostrarLista = false
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val progreso =
                                    if (bolsa.pesoInicial <= 0) 0f else (bolsa.pesoRestante / bolsa.pesoInicial).toFloat()
                                CircularProgressIndicator(
                                    progress = { progreso.coerceIn(0f, 1f) },
                                    modifier = Modifier.size(36.dp),
                                    strokeWidth = 4.dp,
                                    color = XantinaPrimary
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(bolsa.nombre, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                    Text(
                                        "${bolsa.pesoRestante.roundToInt()} g de ${bolsa.pesoInicial.roundToInt()} g",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        cafeSeleccionado?.let { bolsa ->
            val restanteEstimado = (bolsa.pesoRestante - cantidadCafe).coerceAtLeast(0.0)
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val progreso =
                        if (bolsa.pesoInicial <= 0) 0f else (restanteEstimado / bolsa.pesoInicial).toFloat()
                    CircularProgressIndicator(
                        progress = { progreso.coerceIn(0f, 1f) },
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 5.dp,
                        color = XantinaPrimary
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "${restanteEstimado.roundToInt()} g restantes después de la extracción",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Bolsa actual: ${bolsa.pesoRestante.roundToInt()} g disponibles",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // Card de cantidades
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // ---- Cantidad café ----
                Text(
                    "Cantidad de café (g):",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Slider(
                    value = cantidadCafe.toFloat(),
                    onValueChange = { cantidadCafe = it.toInt() },
                    valueRange = 5f..30f
                )
                Text(
                    "$cantidadCafe g",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = XantinaPrimary
                )

                Spacer(Modifier.height(20.dp))

                // ---- Cantidad agua ----
                Text(
                    "Cantidad de agua (ml):",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = cantidadAgua.toString(),
                    onValueChange = { value ->
                        val newValue = value.toIntOrNull()
                        if (newValue != null && newValue > 0) {
                            cantidadAgua = newValue
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))
                val ratioTextoActual = metodo.ratio ?: "1:$ratioRecomendado"
                Text(
                    "Ratio recomendado: $cantidadCafe g × $ratioTextoActual ≈ $cantidadAgua ml",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Card de tiempo estimado
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = XantinaSecondary.copy(alpha = 0.15f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⏱",
                    fontSize = 28.sp
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Tiempo estimado de preparación",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "$tiempoEstimadoMinutos minutos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = XantinaSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                cafeSeleccionado?.let { bolsa ->
                    onStart(bolsa.id, cantidadCafe)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = cafeSeleccionado != null,
            colors = ButtonDefaults.buttonColors(containerColor = XantinaPrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                "Iniciar extracción",
                color = MaterialTheme.colorScheme.surface,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
