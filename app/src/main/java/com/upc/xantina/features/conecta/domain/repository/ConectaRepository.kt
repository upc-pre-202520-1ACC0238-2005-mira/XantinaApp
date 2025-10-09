package com.upc.xantina.features.conecta.domain.repository

import com.upc.xantina.features.conecta.domain.model.Publicacion

interface ConectaRepository {
    fun obtenerPublicaciones(): List<Publicacion>
}
