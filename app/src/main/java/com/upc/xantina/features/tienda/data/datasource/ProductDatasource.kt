package com.upc.xantina.features.tienda.data.datasource

import com.upc.xantina.features.tienda.domain.model.Product

class ProductDatasource {

    // Lista simulada de productos
    fun getProducts(): List<Product> = listOf(
        Product(
            id = "1",
            name = "Caffe Mocha",
            price = 4.53,
            description = "A cappuccino is an approximately 150 ml (5 oz) beverage, with 25 ml of espresso coffee and 85 ml of fresh milk...",
            emoji = "☕",
            category = "Cafés",
            stock = 10
        ),
        Product(
            id = "2",
            name = "Latte",
            price = 3.99,
            description = "Smooth milk with espresso, perfect for any time of the day...",
            emoji = "🥛",
            category = "Cafés",
            stock = 15
        ),
        Product(
            id = "3",
            name = "Cappuccino",
            price = 4.20,
            description = "Classic Italian coffee with equal parts espresso, steamed milk, and foam...",
            emoji = "☕",
            category = "Cafés",
            stock = 8
        ),
        Product(
            id = "4",
            name = "Iced Coffee",
            price = 3.50,
            description = "Cold coffee served over ice, refreshing and energizing...",
            emoji = "🥶",
            category = "Cafés Fríos",
            stock = 12
        )
    )
}
