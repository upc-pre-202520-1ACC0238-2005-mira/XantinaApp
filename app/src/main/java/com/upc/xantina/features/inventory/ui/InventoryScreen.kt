package com.upc.xantina.features.inventory.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.features.extraccion.domain.model.BolsaCafe
import com.upc.xantina.features.extraccion.domain.model.BolsaCafeInput
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import com.upc.xantina.shared.ui.theme.XantinaSecondary

@Composable
fun InventoryScreen(
    bolsasCafe: List<BolsaCafe>,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onCreateBolsa: (BolsaCafeInput) -> Unit,
    successMessage: String?,
    onConsumeMessage: () -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(successMessage) {
        val mensaje = successMessage ?: return@LaunchedEffect
        if (mensaje.startsWith("Bolsa", ignoreCase = true)) {
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
            onConsumeMessage()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = XantinaPrimary,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar bolsa",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                HeaderSection(onRefresh = onRefresh, isLoading = isLoading)

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = XantinaPrimary)
                    }
                } else {
                    val bolsasDistinct = remember(bolsasCafe) {
                        bolsasCafe.distinctBy { it.id }
                    }

                    if (bolsasDistinct.isEmpty()) {
                        EmptyState()
                    } else {
                        BolsaList(bolsas = bolsasDistinct)
                    }
                }

                errorMessage?.takeIf { it.isNotBlank() }?.let { mensaje ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (showDialog) {
                AddBolsaDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { input ->
                        onCreateBolsa(input)
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    onRefresh: () -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Inventario",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Administra tus bolsas de café",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        IconButton(onClick = onRefresh, enabled = !isLoading) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Actualizar bolsas",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Sin bolsas registradas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Agrega tu primera bolsa para llevar control de tus cafés.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun BolsaList(bolsas: List<BolsaCafe>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        items(bolsas, key = { it.id }) { bolsa ->
            BolsaCard(bolsa = bolsa)
        }
    }
}

@Composable
private fun BolsaCard(bolsa: BolsaCafe) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = bolsa.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            bolsa.origen?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = "Origen: $it",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            bolsa.tostador?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = "Tostador: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Peso inicial",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${bolsa.pesoInicial} g",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Restante",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${bolsa.pesoRestante} g",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = XantinaSecondary
                        )
                    }
                }
            }

            bolsa.varietal?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = "Varietal: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            bolsa.moliendaSugerida?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = "Molienda sugerida: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            bolsa.notas?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun AddBolsaDialog(
    onDismiss: () -> Unit,
    onConfirm: (BolsaCafeInput) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var origen by remember { mutableStateOf("") }
    var tostador by remember { mutableStateOf("") }
    var varietal by remember { mutableStateOf("") }
    var molienda by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var pesoInicial by remember { mutableStateOf("") }
    var pesoRestante by remember { mutableStateOf("") }

    val pesoInicialValido = pesoInicial.toDoubleOrNull()
    val pesoRestanteValido = pesoRestante.toDoubleOrNull()
    val puedeGuardar = nombre.isNotBlank() && pesoInicialValido != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar bolsa de café") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del café *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = origen,
                    onValueChange = { origen = it },
                    label = { Text("Origen") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tostador,
                    onValueChange = { tostador = it },
                    label = { Text("Tostador") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = varietal,
                    onValueChange = { varietal = it },
                    label = { Text("Tipo de grano / varietal") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = molienda,
                    onValueChange = { molienda = it },
                    label = { Text("Molienda sugerida") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pesoInicial,
                    onValueChange = { pesoInicial = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Cantidad de bolsa (g) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pesoRestante,
                    onValueChange = { pesoRestante = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Cantidad actual (g)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Notas de cata") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Los campos marcados con * son obligatorios.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pesoInicialDouble = pesoInicialValido ?: return@Button
                    val input = BolsaCafeInput(
                        nombre = nombre.trim(),
                        pesoInicial = pesoInicialDouble,
                        pesoRestante = pesoRestanteValido ?: pesoInicialDouble,
                        origen = origen.trim().ifBlank { null },
                        tostador = tostador.trim().ifBlank { null },
                        varietal = varietal.trim().ifBlank { null },
                        notas = notas.trim().ifBlank { null },
                        moliendaSugerida = molienda.trim().ifBlank { null }
                    )
                    onConfirm(input)
                },
                enabled = puedeGuardar,
                colors = ButtonDefaults.buttonColors(containerColor = XantinaPrimary)
            ) {
                Text("Guardar", color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

