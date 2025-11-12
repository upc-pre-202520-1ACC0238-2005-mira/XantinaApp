package com.upc.xantina.features.extraccion.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.upc.xantina.core.domain.repository.AuthRepository
import com.upc.xantina.features.social.presentation.viewmodel.SocialViewModel
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import kotlinx.coroutines.launch

@Composable
fun NotasDeCataScreen(
    metodoNombre: String,
    authRepository: AuthRepository,
    onGuardar: () -> Unit,
    onPublicar: () -> Unit,
    socialViewModel: SocialViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var valoracion by remember { mutableStateOf(50f) }
    var acidez by remember { mutableStateOf(50f) }
    var dulzor by remember { mutableStateOf(50f) }
    var amargor by remember { mutableStateOf(50f) }
    var notas by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Notas de Cata", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))
        Text("Valoración general: ${valoracion.toInt()}")
        Slider(value = valoracion, onValueChange = { valoracion = it })

        Spacer(Modifier.height(16.dp))
        Text("Perfil sensorial", fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(8.dp))
        Text("Acidez: ${acidez.toInt()}"); Slider(acidez, { acidez = it })
        Text("Dulzor: ${dulzor.toInt()}"); Slider(dulzor, { dulzor = it })
        Text("Amargor: ${amargor.toInt()}"); Slider(amargor, { amargor = it })

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = notas,
            onValueChange = { notas = it },
            label = { Text("Notas sensoriales") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    scope.launch {
                        val token = authRepository.getAuthToken()
                        if (token != null) {
                            // Guardar en historial (próxima implementación)
                            // TODO: Llamar al endpoint de historial
                            onGuardar()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = XantinaPrimary)
            ) { Text("Guardar") }

            Button(
                onClick = {
                    scope.launch {
                        val token = authRepository.getAuthToken()
                        if (token != null) {
                            val contenido = buildString {
                                append("Extracción con $metodoNombre\n\n")
                                append("Valoración: ${valoracion.toInt()}/100\n\n")
                                append("Perfil Sensorial:\n")
                                append("• Acidez: ${acidez.toInt()}/100\n")
                                append("• Dulzor: ${dulzor.toInt()}/100\n")
                                append("• Amargor: ${amargor.toInt()}/100")
                                if (notas.isNotBlank()) {
                                    append("\n\nNotas: $notas")
                                }
                            }
                            
                            socialViewModel.createPost(token, contenido, null, null)
                            onPublicar()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = XantinaPrimary)
            ) { Text("Publicar") }
        }
    }
}
