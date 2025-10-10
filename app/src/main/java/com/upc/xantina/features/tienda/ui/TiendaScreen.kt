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

    val products = listOf(
        Product("1", "V60", 15.0, "Método de filtrado manual", "🟤", "Métodos", 10),
        Product("2", "Chemex", 20.0, "Método de extracción manual", "🟡", "Métodos", 8),
        Product("3", "Aeropress", 12.0, "Método rápido y compacto", "🟠", "Métodos", 12),
        Product("4", "Molino eléctrico", 80.0, "Molino de café preciso", "⚙️", "Equipos", 5),
        Product("5", "Espumador", 35.0, "Para hacer cappuccinos", "🥛", "Equipos", 7),
        Product("6", "Balanza", 25.0, "Precisa para recetas", "⚖️", "Equipos", 10),
        Product("7", "Jarra Latte", 18.0, "Jarra para texturizar leche", "🍼", "Accesorios", 15),
        Product("8", "Filtros V60", 5.0, "Filtros de papel", "📄", "Accesorios", 50),
        Product("9", "Termómetro", 10.0, "Controla la temperatura de la leche", "🌡️", "Accesorios", 20),
        Product("10", "Colombia Geisha", 12.0, "Café de especialidad", "☕", "Cafés", 25),
        Product("11", "Ethiopia Yirgacheffe", 10.0, "Café floral y cítrico", "☕", "Cafés", 30),
        Product("12", "Brasil Santos", 8.0, "Café balanceado y dulce", "☕", "Cafés", 40)
    )

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
                            .clickable { onProductClick(product) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(product.emoji, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontSize = 18.sp)
                            Text("$${product.price}", color = Color.DarkGray)
                            Text("Stock: ${product.stock}", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
