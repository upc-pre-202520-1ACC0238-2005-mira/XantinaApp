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
import com.upc.xantina.features.extraccion.ui.ExtraccionScreen
import com.upc.xantina.features.extraccion.ui.ParametrosExtraccionScreen
import com.upc.xantina.features.extraccion.ui.PasoExtraccionScreen
import com.upc.xantina.features.extraccion.ui.NotasDeCataScreen
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

                var showProfile by remember { mutableStateOf(false) }

                var selectedMetodo by remember { mutableStateOf<String?>(null) }
                var extraccionIniciada by remember { mutableStateOf(false) }
                var pasoActual by remember { mutableStateOf(1) }

                var mostrarNotas by remember { mutableStateOf(false) }

                // AUTENTICACION
                if (!isLoggedIn) {
                    AuthScreen(
                        onLoginSuccess = { isLoggedIn = true },
                        onRegisterSuccess = { isLoggedIn = true }
                    )
                    return@XantinaTheme
                }

                // APP
                Scaffold(
                    bottomBar = {
                        if (!showProfile && selectedMetodo == null && !extraccionIniciada && !mostrarNotas) {
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
                        if (extraccionIniciada && pasoActual <= 4) {

                            val pasos = listOf(
                                Triple("Vertido de agua", "Vierte el agua en círculos.", 20),
                                Triple("Reposo inicial", "Deja reposar para que se expanda.", 15),
                                Triple("Remoción", "Mezcla suavemente.", 10),
                                Triple("Reposo final", "Espera a que termine.", 20)
                            )

                            val paso = pasos[pasoActual - 1]

                            PasoExtraccionScreen(
                                pasoActual = pasoActual,
                                totalPasos = 4,
                                metodo = selectedMetodo ?: "",
                                titulo = paso.first,
                                instruccion = paso.second,
                                duracionSegundos = paso.third,
                                onPasoCompleto = {
                                    if (pasoActual < 4) {
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
                                onNavigateToCreate = { },
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
