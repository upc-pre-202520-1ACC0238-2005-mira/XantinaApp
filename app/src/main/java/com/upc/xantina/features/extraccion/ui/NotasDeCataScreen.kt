package com.upc.xantina.features.extraccion.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.shared.ui.theme.XantinaPrimary

@Composable
fun NotasDeCataScreen(
    onGuardar: () -> Unit,
    onPublicar: () -> Unit
) {
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
                onClick = onGuardar,
                colors = ButtonDefaults.buttonColors(containerColor = XantinaPrimary)
            ) { Text("Guardar") }

            Button(
                onClick = onPublicar,
                colors = ButtonDefaults.buttonColors(containerColor = XantinaPrimary)
            ) { Text("Publicar") }
        }
    }
}
