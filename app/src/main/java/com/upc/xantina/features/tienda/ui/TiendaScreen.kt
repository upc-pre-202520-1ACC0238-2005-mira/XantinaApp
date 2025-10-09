package com.upc.xantina.features.tienda.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.features.tienda.domain.model.Product
import com.upc.xantina.features.tienda.domain.repository.TiendaRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiendaScreen(
    repository: TiendaRepository,
    cartItems: MutableList<Product>,
    onCartClick: () -> Unit,
    onProductClick: (Product) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Todos") }
    val products = repository.getProducts()
    val filters = listOf("Todos", "Métodos", "Equipos", "Accesorios", "Cafés")
    val filteredProducts = if (selectedFilter == "Todos") products else products.filter { it.category == selectedFilter }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda 🛒", color = Color.White) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF795548)),
                actions = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.LightGray, CircleShape)
                            .clickable { onCartClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (cartItems.isNotEmpty()) "🛒(${cartItems.size})" else "🛒",
                            fontSize = 16.sp
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    Button(
                        onClick = { selectedFilter = filter },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedFilter == filter) Color(0xFF5D4037) else Color.LightGray
                        )
                    ) {
                        Text(filter, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(filteredProducts) { product ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color(0xFFF5F5F5), MaterialTheme.shapes.medium)
                            .padding(16.dp)
                            .clickable { onProductClick(product) }, // click abre detalle
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(product.emoji, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontSize = 18.sp)
                            Text("$${product.price}", color = Color.DarkGray)
                            Text("Stock: ${product.stock}", color = Color.Gray, fontSize = 12.sp)
                        }
                        Button(onClick = { cartItems.add(product) }) {
                            Text("Agregar")
                        }
                    }
                }
            }
        }
    }
}
