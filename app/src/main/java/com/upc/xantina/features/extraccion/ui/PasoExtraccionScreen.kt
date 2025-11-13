package com.upc.xantina.features.extraccion.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import kotlinx.coroutines.delay

data class PasoExtraccionUi(
    val titulo: String,
    val instruccion: String,
    val duracionSegundos: Int,
    val animacion: PasoAnimacion,
    val recordatorio: String,
    val requiereAccionManual: Boolean = false
)

sealed interface PasoAnimacion {
    data class Lottie(
        val url: String,
        val speed: Float = 1.2f,
        val autoplay: Boolean = true,
        val loop: Boolean = true
    ) : PasoAnimacion
}

fun defaultPasosExtraccion(): List<PasoExtraccionUi> = listOf(
    PasoExtraccionUi(
        titulo = "Vertido de agua",
        instruccion = "Vierte el agua en círculos desde el centro hacia afuera.",
        duracionSegundos = 20,
        animacion = PasoAnimacion.Lottie(
            url = "https://lottie.host/090315c6-44d9-45df-b120-11b6760dd72e/8RD85LN6uY.lottie",
            speed = 3f
        ),
        recordatorio = "Mantén la jarra cerca del filtro para un flujo constante."
    ),
    PasoExtraccionUi(
        titulo = "Reposo inicial",
        instruccion = "Permite que el café florezca y libere CO₂.",
        duracionSegundos = 15,
        animacion = PasoAnimacion.Lottie(
            url = "https://lottie.host/4e7cce53-b9cd-468f-accf-0b9511c28d65/i8hf0Nuvna.lottie",
            speed = 3f
        ),
        recordatorio = "Observa la floración: debe crear una ligera espuma."
    ),
    PasoExtraccionUi(
        titulo = "Remoción",
        instruccion = "Mezcla suavemente con una cuchara para homogenizar.",
        duracionSegundos = 10,
        animacion = PasoAnimacion.Lottie(
            url = "https://lottie.host/090315c6-44d9-45df-b120-11b6760dd72e/8RD85LN6uY.lottie",
            speed = 2.5f
        ),
        recordatorio = "Haz movimientos lentos para evitar romper el filtro."
    ),
    PasoExtraccionUi(
        titulo = "Reposo final",
        instruccion = "Deja que el agua termine de pasar y el café se asiente.",
        duracionSegundos = 20,
        animacion = PasoAnimacion.Lottie(
            url = "https://lottie.host/4e7cce53-b9cd-468f-accf-0b9511c28d65/i8hf0Nuvna.lottie",
            speed = 2.4f
        ),
        recordatorio = "Prepárate para servir: calienta la taza mientras esperas."
    )
)

private enum class PasoActionState { ManualWaiting, ManualReady, AutoWaiting, AutoAdvancing, Paused }

@Composable
private fun PasoProgressIndicator(
    pasoActual: Int,
    totalPasos: Int,
    modifier: Modifier = Modifier
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val completedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
    val inactiveColor = MaterialTheme.colorScheme.surfaceVariant

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val safeTotal = totalPasos.coerceAtLeast(1)
        repeat(safeTotal) { index ->
            val currentIndex = pasoActual.coerceIn(1, safeTotal) - 1
            val isCurrent = index == currentIndex
            val isCompleted = index < currentIndex
            val color = when {
                isCurrent -> activeColor
                isCompleted -> completedColor
                else -> inactiveColor
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(if (isCurrent) 10.dp else 6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )
        }
    }
}

@Composable
fun PasoExtraccionScreen(
    pasoActual: Int,
    totalPasos: Int = 4,
    metodoNombre: String,
    paso: PasoExtraccionUi,
    puedeRetroceder: Boolean,
    onPasoAnterior: () -> Unit,
    onReiniciarPaso: () -> Unit,
    onSalirProceso: () -> Unit,
    onPasoCompleto: () -> Unit
) {
    val duracion = paso.duracionSegundos.coerceAtLeast(0)
    var tiempoRestante by remember { mutableIntStateOf(duracion) }
    var isPaused by remember { mutableStateOf(false) }

    LaunchedEffect(pasoActual, paso) {
        tiempoRestante = duracion
        isPaused = false

        if (duracion > 0) {
            while (tiempoRestante > 0) {
                if (!isPaused) {
                    delay(1000)
                    tiempoRestante--
                } else {
                    delay(300)
                }
            }
        }

        // Si el paso NO requiere acción manual, avanza automáticamente cuando el timer llegue a 0
        if (!paso.requiereAccionManual && tiempoRestante <= 0) {
            delay(500) // Pequeño delay para que el usuario vea que llegó a 0
            onPasoCompleto()
        }
    }

    val progresoObjetivo = if (duracion <= 0) 1f else 1f - (tiempoRestante.toFloat() / duracion.toFloat())
    val progresoAnimado by animateFloatAsState(
        targetValue = progresoObjetivo,
        animationSpec = tween(durationMillis = 600),
        label = "pasoProgress"
    )

    val mostrarRecordatorio by remember {
        derivedStateOf { tiempoRestante in 1 until duracion / 2 + 1 }
    }

    val umbralFinal = remember(duracion) { if (duracion <= 0) 0 else minOf(5, duracion) }
    val estaFinalizando by remember {
        derivedStateOf { duracion > 0 && tiempoRestante in 1..umbralFinal }
    }

    val colorProgreso by animateColorAsState(
        targetValue = if (estaFinalizando) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
        animationSpec = tween(durationMillis = 400),
        label = "colorProgreso"
    )

    val escalaTiempo by animateFloatAsState(
        targetValue = if (estaFinalizando) 1.05f else 1f,
        animationSpec = tween(durationMillis = 450),
        label = "escalaTiempo"
    )

    val minutos = tiempoRestante / 60
    val segundos = tiempoRestante % 60
    val formatoTiempo = String.format("%02d:%02d", minutos, segundos)
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface.copy(alpha = 0.35f)
        )
    )

    val accionState by remember {
        derivedStateOf {
            when {
                isPaused -> PasoActionState.Paused
                paso.requiereAccionManual && tiempoRestante <= 0 -> PasoActionState.ManualReady
                paso.requiereAccionManual -> PasoActionState.ManualWaiting
                tiempoRestante <= 0 -> PasoActionState.AutoAdvancing
                else -> PasoActionState.AutoWaiting
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onSalirProceso,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Salir")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onPasoAnterior,
                    enabled = puedeRetroceder
                ) {
                    Text("Anterior")
                }
                OutlinedButton(
                    onClick = {
                        tiempoRestante = duracion
                        isPaused = false
                        onReiniciarPaso()
                    }
                ) {
                    Text("Reiniciar")
                }
                OutlinedButton(
                    onClick = { isPaused = !isPaused }
                ) {
                    Text(if (isPaused) "Reanudar ▶" else "Pausar ⏸")
                }
            }
        }

        Text(
            text = metodoNombre,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text("Paso $pasoActual de $totalPasos", fontSize = 16.sp)

        Spacer(Modifier.height(24.dp))

        PasoProgressIndicator(
            pasoActual = pasoActual,
            totalPasos = totalPasos
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(paso.titulo, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(paso.instruccion, fontSize = 16.sp)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    when (val animacion = paso.animacion) {
                        is PasoAnimacion.Lottie -> {
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.Url(animacion.url)
                            )
                            val iterations = if (animacion.loop) {
                                LottieConstants.IterateForever
                            } else {
                                1
                            }
                            val progress by animateLottieCompositionAsState(
                                composition = composition,
                                iterations = iterations,
                                isPlaying = animacion.autoplay,
                                speed = animacion.speed,
                                restartOnPlay = true
                            )

                            if (composition != null) {
                                LottieAnimation(
                                    composition = composition,
                                    progress = { progress },
                                    modifier = Modifier
                                        .size(220.dp)
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = mostrarRecordatorio) {
                    Text(
                        text = paso.recordatorio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        LinearProgressIndicator(
            progress = { progresoAnimado.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            color = colorProgreso
        )

        Spacer(Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = escalaTiempo
                    scaleY = escalaTiempo
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tiempo restante",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = formatoTiempo,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Spacer(Modifier.height(8.dp))

        AnimatedContent(
            targetState = accionState,
            transitionSpec = {
                (fadeIn(animationSpec = tween(250)) + scaleIn(initialScale = 0.95f)) togetherWith
                    (fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.95f))
            },
            label = "pasoAction"
        ) { state ->
            when (state) {
                PasoActionState.ManualWaiting -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Completa el paso y espera a que finalice el tiempo.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                PasoActionState.ManualReady -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "¡Listo! Puedes avanzar cuando quieras.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                PasoActionState.AutoWaiting -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Avanzaremos automáticamente cuando termine el tiempo.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                PasoActionState.AutoAdvancing -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "✓ Pasando al siguiente paso...",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                PasoActionState.Paused -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Temporizador en pausa",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val puedeAvanzarManualmente = !paso.requiereAccionManual || tiempoRestante <= 0
        Button(
            onClick = {
                tiempoRestante = 0
                onPasoCompleto()
            },
            enabled = puedeAvanzarManualmente,
            colors = ButtonDefaults.buttonColors(containerColor = XantinaPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Siguiente")
        }
    }
}
