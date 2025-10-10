@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.upc.xantina.features.tienda.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.features.tienda.domain.model.Product
import com.upc.xantina.features.tienda.domain.repository.TiendaRepository


@Composable
fun TiendaScreen(
    repository: TiendaRepository,
    onCartClick: () -> Unit,
    onProductClick: (Product) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Todos") }
    val products = repository.getProducts()
    val filters = listOf("Todos", "Métodos", "Equipos", "Accesorios", "Cafés")

    val filteredProducts = if (selectedFilter == "Todos")
        products
    else
        products.filter { it.category == selectedFilter }

    val cartItems = repository.getCart()
    val favorites = repository.getFavorites()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda 🛍️", color = Color.White) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF795548)),
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { onCartClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (cartItems.isNotEmpty()) "🛒(${cartItems.size})" else "🛒",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
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
                            containerColor = if (selectedFilter == filter)
                                Color(0xFF5D4037)
                            else
                                Color.LightGray
                        )
                    ) {
                        Text(filter, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredProducts) { product ->
                    ProductItem(
                        product = product,
                        isFavorite = favorites.any { it.id == product.id },
                        onFavoriteClick = {
                            if (favorites.any { it.id == product.id }) {
                                repository.removeFromFavorites(product)
                            } else {
                                repository.addToFavorites(product)
                            }
                        },
                        onAddToCart = {
                            repository.addToCart(product)
                        },
                        onClick = { onProductClick(product) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onAddToCart: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), MaterialTheme.shapes.medium)
            .padding(16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(product.emoji, fontSize = 32.sp)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(product.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("S/. ${product.price}", color = Color.DarkGray)
            Text("Stock: ${product.stock}", color = Color.Gray, fontSize = 12.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isFavorite) "❤️" else "🤍",
                fontSize = 22.sp,
                modifier = Modifier
                    .clickable { onFavoriteClick() }
                    .padding(bottom = 8.dp)
            )
            Button(
                onClick = onAddToCart,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D6E63))
            ) {
                Text("Agregar", fontSize = 12.sp, color = Color.White)
            }
        }
    }
}
