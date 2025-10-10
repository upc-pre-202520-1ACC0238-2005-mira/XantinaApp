package com.upc.xantina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.upc.xantina.features.auth.ui.AuthScreen
import com.upc.xantina.features.extraccion.ui.ExtraccionScreen
import com.upc.xantina.features.tienda.data.datasource.ProductDatasource
import com.upc.xantina.features.tienda.data.repository.TiendaRepositoryImpl
import com.upc.xantina.features.tienda.domain.model.OrderItem
import com.upc.xantina.features.tienda.domain.model.Product
import com.upc.xantina.features.tienda.ui.CartScreen
import com.upc.xantina.features.tienda.ui.OrderScreen
import com.upc.xantina.features.tienda.ui.ProductDetailScreen
import com.upc.xantina.features.tienda.ui.TiendaScreen
import com.upc.xantina.features.conecta.ui.ConectaScreen
import com.upc.xantina.features.profile.ui.ProfileScreen
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
                var selectedTab by remember { mutableStateOf(BottomNavTab.EXTRACCION) }

                val tiendaRepository = TiendaRepositoryImpl(ProductDatasource())
                val cartItems = remember { mutableStateListOf<Product>() }
                val favoriteProducts = remember { mutableStateListOf<Product>() }

                var showCart by remember { mutableStateOf(false) }
                var showProductDetail by remember { mutableStateOf<Product?>(null) }
                var showOrderScreen by remember { mutableStateOf<OrderItem?>(null) }
                var showProfile by remember { mutableStateOf(false) }

                if (!isLoggedIn) {
                    AuthScreen(
                        onLoginSuccess = { isLoggedIn = true },
                        onRegisterSuccess = { isLoggedIn = true }
                    )
                } else {
                    Scaffold(
                        bottomBar = {
                            if (!showCart && showProductDetail == null && showOrderScreen == null && !showProfile) {
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
                            when {
                                showCart -> {
                                    CartScreen(
                                        cartItems = cartItems,
                                        onBack = { showCart = false },
                                        onGoToStore = {
                                            showCart = false
                                            selectedTab = BottomNavTab.TIENDA
                                        }
                                    )
                                }

                                showProductDetail != null -> {
                                    ProductDetailScreen(
                                        product = showProductDetail!!,
                                        onAddToCart = { orderItem ->
                                            cartItems.add(orderItem.product)
                                            showOrderScreen = orderItem
                                            showProductDetail = null
                                        },
                                        onBack = { showProductDetail = null }
                                    )
                                }

                                showOrderScreen != null -> {
                                    OrderScreen(
                                        orderItem = showOrderScreen!!,
                                        onBack = { showOrderScreen = null },
                                        onOrderConfirmed = { showOrderScreen = null }
                                    )
                                }

                                showProfile -> {
                                    ProfileScreen(
                                        onBack = { showProfile = false },
                                        favoriteProducts = favoriteProducts
                                    )
                                }

                                else -> {
                                    when (selectedTab) {
                                        BottomNavTab.EXTRACCION -> ExtraccionScreen(
                                            onNavigateToCreate = { /* TODO */ },
                                            onNavigateToAll = { /* TODO */ }
                                        )

                                        BottomNavTab.TIENDA -> TiendaScreen(
                                            repository = tiendaRepository,
                                            onCartClick = { showCart = true },
                                            onProductClick = { product -> showProductDetail = product }
                                        )

                                        BottomNavTab.CONECTA -> ConectaScreen(
                                            onProfileClick = { showProfile = true }
                                        )

                                        else -> {}
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
