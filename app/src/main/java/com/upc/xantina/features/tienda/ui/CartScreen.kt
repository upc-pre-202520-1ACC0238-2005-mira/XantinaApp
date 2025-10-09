package com.upc.xantina.features.tienda.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    onGoToStore: () -> Unit // <-- añadimos callback
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (cartItems.isEmpty()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color.LightGray, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🛒", fontSize = 40.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Tu carrito está vacío", fontSize = 20.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Agrega productos para comenzar tu compra", fontSize = 16.sp, color = Color.Gray)
                    }
                } else {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                Button(
                                    onClick = { cartItems.remove(product) }
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        val total = cartItems.sumOf { it.price }
                        Text("Total: $${total}", fontSize = 18.sp, color = Color.Black)
                    }
                }

                // ---------- Botón "Ir a la tienda" ----------
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onGoToStore,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548)),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp)
                ) {
                    Text("Ir a la tienda", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}
