package com.upc.xantina.features.tienda.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.upc.xantina.features.tienda.domain.model.Product

@Composable
fun TiendaApp(repository: com.upc.xantina.features.tienda.domain.repository.TiendaRepository) {
    val showCart = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val cartItems = remember { mutableStateListOf<Product>() }

    if (showCart.value) {
        CartScreen(
            cartItems = cartItems.toList(),
            onBack = { showCart.value = false }
        )
    } else {
        TiendaScreen(
            repository = repository,
            cartItems = cartItems,
            onCartClick = { showCart.value = true }
        )
    }
}
