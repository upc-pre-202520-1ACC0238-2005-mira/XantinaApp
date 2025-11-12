package com.upc.xantina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.upc.xantina.features.auth.ui.AuthScreen
import com.upc.xantina.features.conecta.ui.ConectaScreen
import com.upc.xantina.features.extraccion.domain.model.MetodoExtraccion
import com.upc.xantina.features.extraccion.ui.CrearMetodoScreen
import com.upc.xantina.features.extraccion.ui.ExtraccionScreen
import com.upc.xantina.features.extraccion.ui.NotasDeCataScreen
import com.upc.xantina.features.extraccion.ui.ParametrosExtraccionScreen
import com.upc.xantina.features.extraccion.ui.PasoExtraccionScreen
import com.upc.xantina.features.extraccion.ui.PasoExtraccionUi
import com.upc.xantina.features.extraccion.ui.defaultPasosExtraccion
import com.upc.xantina.features.profile.ui.ProfileScreen
import com.upc.xantina.shared.ui.components.BottomNavTab
import com.upc.xantina.shared.ui.components.XantinaBottomNavigation
import com.upc.xantina.ui.theme.XantinaTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.upc.xantina.features.auth.presentation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            XantinaTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                val authUiState by authViewModel.uiState.collectAsState()
                val isLoggedIn = authUiState.isAuthenticated
                var selectedTab by remember { mutableStateOf(BottomNavTab.EXTRACCION) }

                var showProfile by remember { mutableStateOf(false) }

                var selectedMetodo by remember { mutableStateOf<MetodoExtraccion?>(null) }
                var extraccionIniciada by remember { mutableStateOf(false) }
                var pasoActual by remember { mutableStateOf(1) }

                var mostrarNotas by remember { mutableStateOf(false) }
                var mostrarCrearMetodo by remember { mutableStateOf(false) }
                var usuarioCreacionId by remember { mutableStateOf<String?>(null) }

                val pasosExtraccion = remember { defaultPasosExtraccion() }

                // AUTENTICACION
                if (!isLoggedIn) {
                    AuthScreen(
                        viewModel = authViewModel
                    )
                    return@XantinaTheme
                }

                // APP
                Scaffold(
                    bottomBar = {
                        if (
                            !showProfile &&
                            selectedMetodo == null &&
                            !extraccionIniciada &&
                            !mostrarNotas &&
                            !mostrarCrearMetodo
                        ) {
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

                        // PERFIL
                        if (showProfile) {
                            ProfileScreen(
                                onBack = { showProfile = false }
                            )
                            return@Box
                        }

                        // CREACIÓN DE MÉTODO
                        if (mostrarCrearMetodo) {
                            CrearMetodoScreen(
                                userId = usuarioCreacionId,
                                onBack = {
                                    mostrarCrearMetodo = false
                                },
                                onCreated = {
                                    mostrarCrearMetodo = false
                                    selectedTab = BottomNavTab.EXTRACCION
                                }
                            )
                            return@Box
                        }

                        // PARÁMETROS DE EXTRACCIÓN
                        if (selectedMetodo != null && !extraccionIniciada && !mostrarNotas) {
                            ParametrosExtraccionScreen(
                                metodo = selectedMetodo!!,
                                onStart = { cafeSeleccionado: String, cantidadCafe: Int, cantidadAgua: Int ->
                                    extraccionIniciada = true
                                    pasoActual = 1
                                },
                                onBack = {
                                    selectedMetodo = null
                                }
                            )
                            return@Box
                        }

                        // PASOS
                        if (extraccionIniciada && pasoActual <= pasosExtraccion.size) {

                            val paso: PasoExtraccionUi = pasosExtraccion[pasoActual - 1]

                            PasoExtraccionScreen(
                                pasoActual = pasoActual,
                                totalPasos = pasosExtraccion.size,
                                metodoNombre = selectedMetodo?.nombre ?: "",
                                paso = paso,
                                onPasoCompleto = {
                                    if (pasoActual < pasosExtraccion.size) {
                                        pasoActual++
                                    } else {
                                        extraccionIniciada = false
                                        mostrarNotas = true
                                    }
                                }
                            )
                            return@Box
                        }

                        // NOTAS DE CATA
                        if (mostrarNotas) {
                            NotasDeCataScreen(
                                onGuardar = {
                                    mostrarNotas = false
                                    selectedMetodo = null
                                    selectedTab = BottomNavTab.EXTRACCION
                                },
                                onPublicar = {
                                    mostrarNotas = false
                                    selectedMetodo = null
                                    selectedTab = BottomNavTab.EXTRACCION
                                }
                            )
                            return@Box
                        }

                        // TABS PRINCIPALES
                        when (selectedTab) {

                            BottomNavTab.EXTRACCION -> ExtraccionScreen(
                                userId = authUiState.user?.id,
                                onNavigateToCreate = { usuarioId ->
                                    usuarioCreacionId = usuarioId
                                    mostrarCrearMetodo = true
                                },
                                onNavigateToAll = { },
                                onMethodClick = { metodo ->
                                    selectedMetodo = metodo
                                },
                                onRecentClick = { }
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
