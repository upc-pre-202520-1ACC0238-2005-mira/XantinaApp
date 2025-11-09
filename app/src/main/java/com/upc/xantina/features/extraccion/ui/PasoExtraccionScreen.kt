package com.upc.xantina.features.extraccion.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.upc.xantina.shared.ui.theme.XantinaPrimary

@Composable
fun PasoExtraccionScreen(
    pasoActual: Int,
    totalPasos: Int = 4,
    metodo: String,
    titulo: String,
    instruccion: String,
    duracionSegundos: Int,
    onPasoCompleto: () -> Unit
) {
    var tiempoRestante by remember { mutableStateOf(duracionSegundos) }

    LaunchedEffect(pasoActual) {
        tiempoRestante = duracionSegundos
        while (tiempoRestante > 0) {
            delay(1000)
            tiempoRestante--
        }
    }

    val progreso = 1f - (tiempoRestante.toFloat() / duracionSegundos.toFloat())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(metodo, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Paso $pasoActual de $totalPasos", fontSize = 16.sp)

        Spacer(Modifier.height(24.dp))
        Text(titulo, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(instruccion, fontSize = 16.sp)

        Spacer(Modifier.height(32.dp))
        LinearProgressIndicator(progress = progreso, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))
        Text("Tiempo restante: ${tiempoRestante}s")

        Spacer(Modifier.height(32.dp))

        AnimatedVisibility(visible = tiempoRestante <= 0) {
            Button(
                onClick = onPasoCompleto,
                colors = ButtonDefaults.buttonColors(containerColor = XantinaPrimary)
            ) {
                Text("Siguiente")
            }
        }
    }
}
