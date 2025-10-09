package com.upc.xantina.features.tienda.domain.model

data class Product(
    val id: Int,
    val emoji: String,
    val name: String,
    val price: Double,
    val stock: Int,
    val category: String
)
