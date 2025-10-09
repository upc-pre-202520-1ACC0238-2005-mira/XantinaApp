package com.upc.xantina.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.upc.xantina.ui.screens.ExtraeScreen
import com.upc.xantina.ui.screens.TiendaScreen
import com.upc.xantina.ui.screens.ConectaScreen

@Composable
fun NavGraph(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Screen.Extrae.route
    ) {
        composable(Screen.Extrae.route) { ExtraeScreen() }
        composable(Screen.Tienda.route) { TiendaScreen() }
        composable(Screen.Conecta.route) { ConectaScreen() }
    }
}
