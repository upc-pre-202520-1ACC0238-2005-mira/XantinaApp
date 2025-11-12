package com.upc.xantina.features.extraccion.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.features.extraccion.domain.model.MetodoExtraccion
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import com.upc.xantina.shared.ui.theme.XantinaSecondary

@Composable
fun ParametrosExtraccionScreen(
    metodo: MetodoExtraccion,
    onStart: (String, Int, Int) -> Unit,
    onBack: () -> Unit
) {
    val cafesDisponibles = listOf(
        "Café Etiopía - 250g",
        "Café Colombia - 200g",
        "Café Perú - 150g",
        "Café Brasil - 300g"
    )

    var cafeSeleccionado by remember { mutableStateOf<String?>(null) }
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

    val tiempoEstimadoMinutos = 2.5

    // Recalcular agua basado en ratio
    LaunchedEffect(cantidadCafe, ratioRecomendado) {
        cantidadAgua = cantidadCafe * ratioRecomendado
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

        metodo.ratio?.let {
            Text("Ratio sugerido: $it", fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
        }

        // ---- Selección de café ----
        Text("Selecciona un café", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { mostrarLista = !mostrarLista }
        ) {
            OutlinedTextField(
                value = cafeSeleccionado ?: "",
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
                    items(cafesDisponibles) { cafe ->
                        Text(
                            cafe,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clickable {
                                    cafeSeleccionado = cafe
                                    mostrarLista = false
                                }
                        )
                    }
                }
            }
        }


        Spacer(Modifier.height(24.dp))

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
        val ratioTexto = metodo.ratio ?: "1:$ratioRecomendado"
        Text("Ratio recomendado: $cantidadCafe g × $ratioTexto ≈ $cantidadAgua ml", fontSize = 14.sp)
        Spacer(Modifier.height(16.dp))

        Text("Tiempo estimado: $tiempoEstimadoMinutos min", color = XantinaSecondary)

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                if (cafeSeleccionado != null) {
                    onStart(cafeSeleccionado!!, cantidadCafe, cantidadAgua)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = XantinaPrimary)
        ) {
            Text("Start", color = MaterialTheme.colorScheme.surface)
        }
    }
}
