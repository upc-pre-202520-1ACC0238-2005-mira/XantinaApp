package com.upc.xantina.features.tienda.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row {
                listOf("Delivery", "Pick Up").forEach { method ->
                    Button(
                        onClick = { selectedMethod = method },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedMethod == method) Color(0xFF795548) else Color.LightGray
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(method, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Product: ${orderItem.product.name}")
            Text("Quantity: $quantity")
            Text("Size: ${orderItem.size}")
            Text("Temperature: ${orderItem.temperature}")
            Text("Note: ${orderItem.note}")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onOrderConfirmed,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548))
            ) {
                Text("Order", color = Color.White)
            }
        }
    }
}
