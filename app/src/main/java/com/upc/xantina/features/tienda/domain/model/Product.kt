package com.upc.xantina.features.tienda.domain.model

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String,
    val emoji: String,
    val category: String,
    val stock: Int
)
