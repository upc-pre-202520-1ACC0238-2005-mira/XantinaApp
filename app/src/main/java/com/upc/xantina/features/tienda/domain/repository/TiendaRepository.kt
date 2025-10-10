package com.upc.xantina.features.tienda.domain.repository

import com.upc.xantina.features.tienda.domain.model.Product

interface TiendaRepository {
    fun getProducts(): List<Product>
    fun addToCart(product: Product)
    fun getCart(): List<Product>
    fun getCartTotal(): Double
    fun addToFavorites(product: Product)
    fun removeFromFavorites(product: Product)
    fun getFavorites(): List<Product>
}
