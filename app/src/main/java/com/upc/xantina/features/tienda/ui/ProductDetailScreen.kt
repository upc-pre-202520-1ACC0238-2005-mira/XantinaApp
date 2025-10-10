package com.upc.xantina.features.tienda.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.R
import com.upc.xantina.features.tienda.domain.model.OrderItem
import com.upc.xantina.features.tienda.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    onAddToCart: (OrderItem) -> Unit,
    onBack: () -> Unit,
    onAddToFavorites: (Product) -> Unit = {}
) {
    var selectedSize by remember { mutableStateOf("M") }
    var selectedTemperature by remember { mutableStateOf("Hot") }
    var isFavorite by remember { mutableStateOf(false) }

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
                },
                actions = {
                    IconButton(
                        onClick = {
                            isFavorite = !isFavorite
                            if (isFavorite) onAddToFavorites(product)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Add to favorites",
                            tint = if (isFavorite) Color.Red else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF795548),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            // Imagen del producto o placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFFD7CCC8)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.coffee_placeholder
                    ),
                    contentDescription = "Product image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(product.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("⭐ 4.8 (230 reviews)", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Text(product.description, fontSize = 16.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Tamaño:", fontSize = 16.sp)
                Row {
                    listOf("S", "M", "L").forEach { size ->
                        Button(
                            onClick = { selectedSize = size },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedSize == size)
                                    Color(0xFF795548)
                                else
                                    Color.LightGray
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(size, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Temperatura:", fontSize = 16.sp)
                Row {
                    listOf("Hot", "Iced").forEach { temp ->
                        Button(
                            onClick = { selectedTemperature = temp },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTemperature == temp)
                                    Color(0xFF795548)
                                else
                                    Color.LightGray
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(temp, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val orderItem = OrderItem(
                            product = product,
                            quantity = 1,
                            size = selectedSize,
                            temperature = selectedTemperature,
                            note = ""
                        )
                        onAddToCart(orderItem)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548))
                ) {
                    Text("Buy Now - S/.${product.price}", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}
