package com.upc.xantina.features.tienda.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.features.tienda.domain.model.OrderItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    orderItem: OrderItem,
    onBack: () -> Unit,
    onOrderConfirmed: () -> Unit
) {
    var quantity by remember { mutableStateOf(orderItem.quantity) }
    var selectedMethod by remember { mutableStateOf("Delivery") }
    var address by remember { mutableStateOf("Av. Los Olivos 234, Lima") }
    var note by remember { mutableStateOf(TextFieldValue(orderItem.note)) }

    val deliveryCost = if (orderItem.product.price * quantity > 50) 0.0 else 2.5
    val total = orderItem.product.price * quantity + deliveryCost

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order") },
                navigationIcon = {
                    Text(
                        "⬅️",
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(12.dp)
                    )
                }
            )
        }
    ) { padding ->
        // Scroll activado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()) // permite scrollear
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Imagen simulada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFD7CCC8)),
                contentAlignment = Alignment.Center
            ) {
                Text("☕ ${orderItem.product.name}", fontSize = 24.sp)
            }

            // Método de entrega
            Text("Delivery Method", fontSize = 18.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Delivery", "Pick Up").forEach { method ->
                    Button(
                        onClick = { selectedMethod = method },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedMethod == method) Color(0xFF795548) else Color.LightGray
                        )
                    ) {
                        Text(method, color = Color.White)
                    }
                }
            }

            // Dirección si es Delivery
            if (selectedMethod == "Delivery") {
                Text("Address", fontSize = 18.sp)
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF795548),
                        unfocusedIndicatorColor = Color.Gray
                    )
                )
                TextButton(onClick = { /* lógica editar dirección */ }) {
                    Text("✏️ Edit Address", color = Color(0xFF795548))
                }
            }

            // Cantidad
            Text("Quantity", fontSize = 18.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { if (quantity > 1) quantity-- },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D6E63))
                ) {
                    Text("➖", color = Color.White, fontSize = 20.sp)
                }

                Text(
                    "$quantity",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Button(
                    onClick = { quantity++ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D6E63))
                ) {
                    Text("➕", color = Color.White, fontSize = 20.sp)
                }
            }

            // Nota adicional
            Text("Note", fontSize = 18.sp)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("Add a note...") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color(0xFF795548),
                    unfocusedIndicatorColor = Color.Gray
                )
            )

            Divider(color = Color.LightGray, thickness = 1.dp)

            // Resumen del pedido
            Text("Summary", fontSize = 20.sp)
            Text("Product: ${orderItem.product.name}")
            Text("Size: ${orderItem.size}")
            Text("Temperature: ${orderItem.temperature}")
            Text("Price: $${orderItem.product.price}")
            if (selectedMethod == "Delivery") {
                Text(
                    text = if (deliveryCost == 0.0)
                        "Delivery: FREE 🚚"
                    else
                        "Delivery: $${"%.2f".format(deliveryCost)}"
                )
            }
            Text("Total: $${"%.2f".format(total)}", fontSize = 18.sp, color = Color(0xFF795548))

            Spacer(modifier = Modifier.height(12.dp))

            // Botón Confirm Order visible siempre (scroll incluido)
            Button(
                onClick = onOrderConfirmed,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548))
            ) {
                Text("Confirm Order", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
