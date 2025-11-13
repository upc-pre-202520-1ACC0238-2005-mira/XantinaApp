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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Barra superior con controles
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Salir
                OutlinedButton(
                    onClick = onSalirProceso,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Salir",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Salir", fontSize = 14.sp)
                }

                // Controles de navegación
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onPasoAnterior,
                        enabled = puedeRetroceder
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Anterior",
                            tint = if (puedeRetroceder) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                    IconButton(
                        onClick = {
                            tiempoRestante = duracion
                            isPaused = false
                            onReiniciarPaso()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reiniciar"
                        )
                    }
                    OutlinedButton(
                        onClick = { isPaused = !isPaused },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Close,
                            contentDescription = if (isPaused) "Reanudar" else "Pausar",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isPaused) "Reanudar" else "Pausar",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Título y paso actual
        Text(
            text = metodoNombre,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Paso $pasoActual de $totalPasos",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(20.dp))

        PasoProgressIndicator(
            pasoActual = pasoActual,
            totalPasos = totalPasos
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F0E8) // Color crema cálido
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE8DCC8) // Color beige cálido
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
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
                    color = Color(0xFF5D4037), // Marrón oscuro
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatoTiempo,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6F4E37), // Color café
                    fontSize = 36.sp
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
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF8E1) // Amarillo suave
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = "Completa el paso y espera a que finalice el tiempo.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF5D4037)
                        )
                    }
                }
                PasoActionState.ManualReady -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFC8E6C9) // Verde suave
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = "¡Listo! Puedes avanzar cuando quieras.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
                PasoActionState.AutoWaiting -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD) // Azul suave
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = "Avanzaremos automáticamente cuando termine el tiempo.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1565C0)
                        )
                    }
                }
                PasoActionState.AutoAdvancing -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF81C784) // Verde brillante
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Text(
                            text = "✓ Pasando al siguiente paso...",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                PasoActionState.Paused -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFCDD2) // Rojo suave
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = "Temporizador en pausa",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFC62828)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Botón Continuar - Siempre visible con color café
        val puedeAvanzarManualmente = !paso.requiereAccionManual || tiempoRestante <= 0
        val colorCafe = Color(0xFF6F4E37) // Color café/marrón
        
        Button(
            onClick = {
                tiempoRestante = 0
                onPasoCompleto()
            },
            enabled = true,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorCafe,
                disabledContainerColor = colorCafe.copy(alpha = 0.6f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 8.dp
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Continuar",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }
        }
        
        Spacer(Modifier.height(16.dp))
    }
}
