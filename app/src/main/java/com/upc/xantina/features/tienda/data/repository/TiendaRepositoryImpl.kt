package com.upc.xantina.features.tienda.data.repository

import com.upc.xantina.features.tienda.data.datasource.ProductDatasource
import com.upc.xantina.features.tienda.domain.model.Product
import com.upc.xantina.features.tienda.domain.repository.TiendaRepository

class TiendaRepositoryImpl(
    private val datasource: ProductDatasource
) : TiendaRepository {

    override fun getProducts(): List<Product> {
        return datasource.getProducts()
    }
}
