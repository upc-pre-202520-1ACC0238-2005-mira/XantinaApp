package com.upc.xantina.features.tienda.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.features.tienda.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: MutableList<Product>,
    onBack: () -> Unit,
    onGoToStore: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito 🛒") },
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
            if (cartItems.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tu carrito está vacío", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onGoToStore,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548))
                    ) {
                        Text("Ir a la tienda", color = Color.White)
                    }
                }
            } else {
                cartItems.forEach { product ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(product.emoji, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontSize = 18.sp)
                            Text("$${product.price}", color = Color.DarkGray)
                        }
                        Button(onClick = { cartItems.remove(product) }) {
                            Text("Eliminar")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                val total = cartItems.sumOf { it.price }
                Text("Total: $${total}", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onGoToStore,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548))
                ) {
                    Text("Ir a la tienda", color = Color.White)
                }
            }
        }
    }
}
