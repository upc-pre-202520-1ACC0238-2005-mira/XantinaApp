package com.upc.xantina.features.tienda.domain.model

data class OrderItem(
    val product: Product,
    val quantity: Int,
    val size: String,
    val temperature: String,
    val note: String
)
