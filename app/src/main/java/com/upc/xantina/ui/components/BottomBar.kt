package com.upc.xantina.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import com.upc.xantina.navigation.Screen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person

@Composable
fun BottomBar(navController: NavController) {
    val screens = listOf(
        Screen.Extrae,
        Screen.Tienda,
        Screen.Conecta
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        screens.forEach { screen ->
            val icon = when (screen) {
                Screen.Extrae -> Icons.Filled.Home
                Screen.Tienda -> Icons.Filled.ShoppingCart
                Screen.Conecta -> Icons.Filled.Person
            }
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Extrae.route)
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Icon(icon, contentDescription = screen.title) },
                label = { Text(screen.title) }
            )
        }
    }
}
