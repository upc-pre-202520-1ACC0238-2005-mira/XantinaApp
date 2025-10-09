package com.upc.xantina.navigation

sealed class Screen(val route: String, val title: String) {
    object Extrae : Screen("extrae", "Extrae")
    object Tienda : Screen("tienda", "Tienda")
    object Conecta : Screen("conecta", "Conecta")
}
