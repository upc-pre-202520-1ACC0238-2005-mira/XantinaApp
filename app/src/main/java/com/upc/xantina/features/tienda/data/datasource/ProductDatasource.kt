package com.upc.xantina.features.tienda.data.datasource

import com.upc.xantina.features.tienda.domain.model.Product

class ProductDatasource {

    fun getProducts(): List<Product> {
        return listOf(
            Product(1, "🫖", "V60 Cerámica", 2500.0, 5, "Métodos"),
            Product(2, "☕", "Prensa Francesa 1L", 3200.0, 3, "Métodos"),
            Product(3, "⚙️", "Molino Manual", 4500.0, 8, "Equipos"),
            Product(4, "⚖️", "Báscula Digital", 1800.0, 12, "Accesorios"),
            Product(5, "🫘", "Colombia Geisha", 1200.0, 20, "Cafés"),
            Product(6, "🫘", "Etiopía Yirgacheffe", 950.0, 15, "Cafés")
        )
    }
}
