package com.upc.xantina.features.tienda.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.features.tienda.domain.model.OrderItem
import com.upc.xantina.features.tienda.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    onAddToCart: (OrderItem) -> Unit,
    onBack: () -> Unit
) {
    var selectedSize by remember { mutableStateOf("M") }
    var selectedTemperature by remember { mutableStateOf("Hot") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.name) },
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
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(product.name, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Ice/Hot", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("⭐ 4.8 (230 reviews)", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = product.description,  // ✅ usa description del modelo Product
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                listOf("S", "M", "L").forEach { size ->
                    Button(
                        onClick = { selectedSize = size },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedSize == size) Color(0xFF795548) else Color.LightGray
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(size, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val orderItem = OrderItem(
                        product = product,
                        quantity = 1,
                        size = selectedSize,
                        temperature = selectedTemperature, // ✅ coincide con OrderItem
                        note = "" // ✅ coincide con OrderItem
                    )
                    onAddToCart(orderItem)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548))
            ) {
                Text("Buy Now", color = Color.White)
            }
        }
    }
}
