package com.upc.xantina
import com.upc.xantina.features.tienda.ui.CartScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.upc.xantina.features.auth.ui.AuthScreen
import com.upc.xantina.features.extraccion.ui.ExtraccionScreen
import com.upc.xantina.features.tienda.data.datasource.ProductDatasource
import com.upc.xantina.features.tienda.data.repository.TiendaRepositoryImpl
import com.upc.xantina.features.tienda.domain.model.Product
import com.upc.xantina.features.tienda.ui.TiendaScreen
import com.upc.xantina.shared.ui.components.BottomNavTab
import com.upc.xantina.shared.ui.components.XantinaBottomNavigation
import com.upc.xantina.ui.theme.XantinaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XantinaTheme {

                var isLoggedIn by remember { mutableStateOf(false) }

                // Tab seleccionado para BottomNavigation
                var selectedTab by remember { mutableStateOf(BottomNavTab.EXTRACCION) }

                // Estado de pantalla de carrito
                var showCart by remember { mutableStateOf(false) }

                // Repositorio Tienda y carrito
                val tiendaRepository = TiendaRepositoryImpl(ProductDatasource())
                val cartItems = remember { mutableStateListOf<Product>() }

                if (!isLoggedIn) {
                    AuthScreen(
                        onLoginSuccess = { isLoggedIn = true },
                        onRegisterSuccess = { isLoggedIn = true }
                    )
                } else {
                    Scaffold(
                        bottomBar = {
                            if (!showCart) { // ocultar bottom nav al mostrar carrito
                                XantinaBottomNavigation(
                                    selectedTab = selectedTab,
                                    onTabSelected = { selectedTab = it }
                                )
                            }
                        }
                    ) { padding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                        ) {
                            if (showCart) {
                                CartScreen(
                                    cartItems = cartItems,
                                    onBack = { showCart = false },
                                    onGoToStore = { showCart = false }
                                )
                            } else {
                                when (selectedTab) {
                                    BottomNavTab.EXTRACCION -> ExtraccionScreen(
                                        onNavigateToCreate = { /* TODO */ },
                                        onNavigateToAll = { /* TODO */ }
                                    )
                                    BottomNavTab.TIENDA -> TiendaScreen(
                                        repository = tiendaRepository,
                                        cartItems = cartItems,
                                        onCartClick = { showCart = true } // Mostrar carrito
                                    )
                                    BottomNavTab.CONECTA -> {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Pantalla Conecta")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ExtraccionScreenPreview() {
    XantinaTheme { ExtraccionScreen() }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    XantinaTheme { AuthScreen() }
}

@Preview(showBackground = true)
@Composable
fun TiendaScreenPreview() {
    XantinaTheme {
        val repository = TiendaRepositoryImpl(ProductDatasource())
        val cartItems = remember { mutableStateListOf<Product>() }
        TiendaScreen(
            repository = repository,
            cartItems = cartItems,
            onCartClick = {}
        )
    }
}
