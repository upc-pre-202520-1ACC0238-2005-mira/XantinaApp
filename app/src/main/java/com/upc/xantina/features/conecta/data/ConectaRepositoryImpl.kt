package com.upc.xantina.features.conecta.data

import com.upc.xantina.features.conecta.domain.model.Publicacion
import com.upc.xantina.features.conecta.domain.repository.ConectaRepository

class ConectaRepositoryImpl : ConectaRepository {
    override fun obtenerPublicaciones(): List<Publicacion> {
        return listOf(
            Publicacion(1, "Ana Martínez", "Hace 2 horas", "V60", "Colombia Geisha", "☕", 5, 24),
            Publicacion(2, "Carlos López", "Hace 5 horas", "Prensa Francesa", "Brasil Natural", "☕", 4, 18),
            Publicacion(3, "María Silva", "Hace 1 día", "V60", "Etiopía Yirgacheffe", "☕", 5, 31)
        )
    }
}
