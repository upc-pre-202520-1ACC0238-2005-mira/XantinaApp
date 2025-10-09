package com.upc.xantina.features.tienda.domain.repository

import com.upc.xantina.features.tienda.domain.model.Product

interface TiendaRepository {
    fun getProducts(): List<Product>
}
