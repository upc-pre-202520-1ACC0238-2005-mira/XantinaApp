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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
            .padding(24.dp),
    ) {

        // Botón regresar
        Text(
            text = "← Regresar",
            fontSize = 16.sp,
            modifier = Modifier
                .clickable { onBack() }
                .padding(bottom = 16.dp)
        )

        // Título
        Text(
            "Parámetros de extracción",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))

        Text("Método: ${metodo.nombre}", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        Text(metodo.descripcion, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))

        val ratioTexto = metodo.ratio ?: "1:$ratioRecomendado"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ratio sugerido: $ratioTexto", fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = { ratioBloqueado = !ratioBloqueado }
            ) {
                Text(
                    text = if (ratioBloqueado) "🔒 Bloqueado" else "🔓 Editable",
                    fontSize = 12.sp
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Spacer(Modifier.height(16.dp))

        // ---- Selección de café ----
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Selecciona un café", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = onRefreshBolsas) {
                Text("Actualizar", fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(8.dp))

        if (isLoadingBolsas) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterHorizontally),
                strokeWidth = 3.dp,
                color = XantinaPrimary
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
                                    progress = progreso.coerceIn(0f, 1f),
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
                        progress = progreso.coerceIn(0f, 1f),
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

        // ---- Cantidad café ----
        Text("Cantidad de café (g):", fontSize = 16.sp)
        Slider(
            value = cantidadCafe.toFloat(),
            onValueChange = { cantidadCafe = it.toInt() },
            valueRange = 5f..30f
        )
        Text("$cantidadCafe g")

        Spacer(Modifier.height(16.dp))

        // ---- Cantidad agua ----
        Text("Cantidad de agua (ml):", fontSize = 16.sp)
        OutlinedTextField(
            value = cantidadAgua.toString(),
            onValueChange = { value ->
                val newValue = value.toIntOrNull()
                if (newValue != null && newValue > 0) {
                    cantidadAgua = newValue
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        val ratioTextoActual = metodo.ratio ?: "1:$ratioRecomendado"
        Text(
            "Ratio recomendado: $cantidadCafe g × $ratioTextoActual ≈ $cantidadAgua ml",
            fontSize = 14.sp
        )
        Spacer(Modifier.height(16.dp))

        Text("Tiempo estimado: $tiempoEstimadoMinutos min", color = XantinaSecondary)

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                cafeSeleccionado?.let { bolsa ->
                    onStart(bolsa.id, cantidadCafe)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = cafeSeleccionado != null,
            colors = ButtonDefaults.buttonColors(containerColor = XantinaPrimary)
        ) {
            Text("Start", color = MaterialTheme.colorScheme.surface)
        }
    }
}
