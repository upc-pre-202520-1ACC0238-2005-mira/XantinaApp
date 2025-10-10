package com.upc.xantina.features.tienda.data.repository

import com.upc.xantina.features.tienda.data.datasource.ProductDatasource
import com.upc.xantina.features.tienda.domain.model.Product
import com.upc.xantina.features.tienda.domain.repository.TiendaRepository

class TiendaRepositoryImpl(
    private val datasource: ProductDatasource
) : TiendaRepository {

    private val cartItems = mutableListOf<Product>()
    private val favoriteItems = mutableSetOf<Product>()

    override fun getProducts(): List<Product> = datasource.getProducts()

    override fun addToCart(product: Product) {
        cartItems.add(product)
    }

    override fun getCart(): List<Product> = cartItems

    override fun getCartTotal(): Double {
        return cartItems.sumOf { it.price }
    }

    override fun addToFavorites(product: Product) {
        favoriteItems.add(product)
    }

    override fun removeFromFavorites(product: Product) {
        favoriteItems.remove(product)
    }

    override fun getFavorites(): List<Product> = favoriteItems.toList()
}
