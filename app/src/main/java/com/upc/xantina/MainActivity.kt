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
import com.upc.xantina.features.extraccion.domain.model.BolsaCafeInput
import com.upc.xantina.features.extraccion.domain.model.Dificultad
import com.upc.xantina.features.social.domain.model.PostExtractionData
import kotlinx.coroutines.launch
import com.upc.xantina.features.extraccion.ui.CrearMetodoScreen
import com.upc.xantina.features.extraccion.ui.ExtraccionScreen
import com.upc.xantina.features.extraccion.ui.NotasDeCataScreen
import com.upc.xantina.features.extraccion.ui.ParametrosExtraccionScreen
import com.upc.xantina.features.extraccion.ui.PasoExtraccionScreen
import com.upc.xantina.features.extraccion.ui.PasoExtraccionUi
import com.upc.xantina.features.extraccion.ui.defaultPasosExtraccion
import com.upc.xantina.features.extraccion.ui.toUiList
import com.upc.xantina.features.inventory.ui.InventoryScreen
import com.upc.xantina.features.profile.ui.ProfileScreen
import com.upc.xantina.shared.ui.components.BottomNavTab
import com.upc.xantina.shared.ui.components.XantinaBottomNavigation
import com.upc.xantina.ui.theme.XantinaTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.upc.xantina.features.auth.presentation.viewmodel.AuthViewModel
import com.upc.xantina.features.extraccion.presentation.viewmodel.ExtraccionViewModel
import com.upc.xantina.core.domain.repository.AuthRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var authRepository: AuthRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            XantinaTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                val authUiState by authViewModel.uiState.collectAsState()
                val extraccionViewModel: ExtraccionViewModel = hiltViewModel()
                val extraccionUiState by extraccionViewModel.uiState.collectAsState()
                val coroutineScope = rememberCoroutineScope()
                
                // Verificar si hay sesión guardada al iniciar
                var isLoggedIn by remember { mutableStateOf(authUiState.isAuthenticated) }
                
                LaunchedEffect(Unit) {
                    // Verificar si hay sesión persistida al iniciar la app
                    coroutineScope.launch {
                        val isAuthenticated = authRepository.isUserAuthenticated()
                        if (isAuthenticated && !authUiState.isAuthenticated) {
                            // Si hay sesión guardada pero el estado no está actualizado, actualizarlo
                            val user = authRepository.getCurrentUser()
                            if (user != null) {
                                authViewModel.restoreSession(user)
                                isLoggedIn = true
                            }
                        } else {
                            isLoggedIn = authUiState.isAuthenticated
                        }
                    }
                }
                
                LaunchedEffect(authUiState.isAuthenticated) {
                    isLoggedIn = authUiState.isAuthenticated
                }
                
                var selectedTab by remember { mutableStateOf(BottomNavTab.EXTRACCION) }

                var showProfile by remember { mutableStateOf(false) }

                var selectedMetodo by remember { mutableStateOf<MetodoExtraccion?>(null) }
                var extraccionIniciada by remember { mutableStateOf(false) }
                var pasoActual by remember { mutableStateOf(1) }
                var bolsaSeleccionadaId by remember { mutableStateOf<String?>(null) }
                var gramosCafeSeleccionados by remember { mutableStateOf(0) }

                var mostrarNotas by remember { mutableStateOf(false) }
                var mostrarCrearMetodo by remember { mutableStateOf(false) }
                var usuarioCreacionId by remember { mutableStateOf<String?>(null) }

                var consumoRegistrado by remember { mutableStateOf(false) }

                var pasosExtraccion by remember { mutableStateOf(defaultPasosExtraccion()) }

                LaunchedEffect(selectedTab) {
                    if (selectedTab == BottomNavTab.TIENDA) {
                        extraccionViewModel.refrescarBolsas()
                    }
                }

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
                                bolsasCafe = extraccionUiState.bolsasCafe,
                                isLoadingBolsas = extraccionUiState.isLoadingBolsas,
                                onRefreshBolsas = { extraccionViewModel.refrescarBolsas() },
                                onStart = { bolsaId: String, cantidadCafe: Int ->
                                    bolsaSeleccionadaId = bolsaId
                                    gramosCafeSeleccionados = cantidadCafe
                                    consumoRegistrado = false
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
                                puedeRetroceder = pasoActual > 1,
                                onPasoAnterior = {
                                    if (pasoActual > 1) {
                                        pasoActual--
                                    }
                                },
                                onReiniciarPaso = {
                                    // Se puede agregar lógica adicional si se requiere
                                },
                                onSalirProceso = {
                                    extraccionIniciada = false
                                    pasoActual = 1
                                    mostrarNotas = false
                                    selectedMetodo = null
                                    bolsaSeleccionadaId = null
                                    consumoRegistrado = false
                                },
                                onPasoCompleto = {
                                    if (pasoActual < pasosExtraccion.size) {
                                        pasoActual++
                                    } else {
                                        if (
                                            !consumoRegistrado &&
                                            bolsaSeleccionadaId != null &&
                                            gramosCafeSeleccionados > 0
                                        ) {
                                            extraccionViewModel.consumirBolsaCafe(
                                                bolsaId = bolsaSeleccionadaId!!,
                                                gramos = gramosCafeSeleccionados.toDouble()
                                            )
                                            extraccionViewModel.refrescarBolsas()
                                            consumoRegistrado = true
                                        }
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
                                metodoNombre = selectedMetodo?.nombre ?: "Método desconocido",
                                authRepository = authRepository,
                                onGuardar = {
                                    mostrarNotas = false
                                    selectedMetodo = null
                                    selectedTab = BottomNavTab.EXTRACCION
                                },
                                onPublicar = {
                                    mostrarNotas = false
                                    selectedMetodo = null
                                    selectedTab = BottomNavTab.CONECTA // Navegar a Conecta después de publicar
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
                                    bolsaSeleccionadaId = null
                                    pasosExtraccion = metodo.configuracion?.steps?.toUiList()
                                        ?: defaultPasosExtraccion()
                                    extraccionViewModel.refrescarBolsas()
                                },
                                onRecentClick = { },
                                viewModel = extraccionViewModel
                            )

                            BottomNavTab.TIENDA -> InventoryScreen(
                                bolsasCafe = extraccionUiState.bolsasCafe,
                                isLoading = extraccionUiState.isLoadingBolsas,
                                errorMessage = extraccionUiState.errorMessage,
                                onRefresh = { extraccionViewModel.refrescarBolsas() },
                                onCreateBolsa = { input ->
                                    extraccionViewModel.crearBolsaCafe(input)
                                },
                                successMessage = extraccionUiState.successMessage,
                                onConsumeMessage = { extraccionViewModel.consumirMensajes() }
                            )

                            BottomNavTab.CONECTA -> ConectaScreen(
                                authRepository = authRepository,
                                onFollowRecipe = { extractionData ->
                                    // Usar los datos completos de la receta para cargar los valores automáticamente
                                    
                                    // Crear configuración con los valores base si están disponibles
                                    val socialConfig = extractionData.configuracion
                                    val configuracion: com.upc.xantina.features.extraccion.domain.model.ConfiguracionMetodo? = 
                                        if (extractionData.gramosCafe != null && extractionData.mililitrosAgua != null) {
                                            val base = com.upc.xantina.features.extraccion.domain.model.BaseParametros(
                                                cafeG = extractionData.gramosCafe.toInt(),
                                                aguaTotalMl = extractionData.mililitrosAgua.toInt()
                                            )
                                            
                                            // Si hay configuración con pasos, convertirla
                                            val pasos = socialConfig?.steps?.map { paso ->
                                                com.upc.xantina.features.extraccion.domain.model.PasoExtraccion(
                                                    step = paso.step,
                                                    timeStart = paso.timeStart,
                                                    timeEnd = paso.timeEnd,
                                                    action = paso.action,
                                                    waterMl = paso.waterMl?.toInt() ?: 0,
                                                    calculation = paso.calculation,
                                                    requiereAccionManual = false
                                                )
                                            } ?: emptyList()
                                            
                                            com.upc.xantina.features.extraccion.domain.model.ConfiguracionMetodo(
                                                grind = socialConfig?.grind ?: "medio",
                                                temperature = socialConfig?.temperature ?: extractionData.temperaturaAgua?.toString() ?: "92",
                                                base = base,
                                                totalTimeSeconds = extractionData.tiempoExtraccion ?: socialConfig?.totalTimeSeconds ?: 120,
                                                steps = pasos
                                            )
                                        } else {
                                            socialConfig?.let { config ->
                                                val pasos = config.steps?.map { paso ->
                                                    com.upc.xantina.features.extraccion.domain.model.PasoExtraccion(
                                                        step = paso.step,
                                                        timeStart = paso.timeStart,
                                                        timeEnd = paso.timeEnd,
                                                        action = paso.action,
                                                        waterMl = paso.waterMl?.toInt() ?: 0,
                                                        calculation = paso.calculation,
                                                        requiereAccionManual = false
                                                    )
                                                } ?: emptyList()
                                                
                                                com.upc.xantina.features.extraccion.domain.model.ConfiguracionMetodo(
                                                    grind = config.grind ?: "medio",
                                                    temperature = config.temperature ?: extractionData.temperaturaAgua?.toString() ?: "92",
                                                    base = config.base?.let { baseConfig ->
                                                        com.upc.xantina.features.extraccion.domain.model.BaseParametros(
                                                            cafeG = baseConfig.cafeG?.toInt() ?: 15,
                                                            aguaTotalMl = baseConfig.aguaTotalMl?.toInt() ?: 225
                                                        )
                                                    } ?: com.upc.xantina.features.extraccion.domain.model.BaseParametros(15, 225),
                                                    totalTimeSeconds = config.totalTimeSeconds ?: extractionData.tiempoExtraccion ?: 120,
                                                    steps = pasos
                                                )
                                            }
                                        }
                                    
                                    val metodoTemporal = MetodoExtraccion(
                                        id = extractionData.recetaId ?: "",
                                        nombre = extractionData.nombre ?: "Receta seguida",
                                        descripcion = extractionData.notas ?: "Receta de la comunidad",
                                        tiempoPreparacion = extractionData.tiempoExtraccion?.let { "$it s" } ?: "—",
                                        icono = extractionData.metodo ?: "coffee",
                                        dificultad = Dificultad.INTERMEDIO,
                                        temperatura = extractionData.temperaturaAgua,
                                        ratio = extractionData.ratio,
                                        creadorId = "", // No tenemos el ID del creador en PostExtractionData
                                        esPublica = true,
                                        configuracion = configuracion
                                    )
                                    selectedMetodo = metodoTemporal
                                    bolsaSeleccionadaId = null
                                    
                                    // Convertir pasos para la UI usando el mapper
                                    if (configuracion != null && configuracion.steps.isNotEmpty()) {
                                        pasosExtraccion = configuracion.steps.toUiList()
                                    } else {
                                        pasosExtraccion = defaultPasosExtraccion()
                                    }
                                    
                                    extraccionViewModel.refrescarBolsas()
                                    
                                    // Navegar a la pantalla de parámetros con los valores precargados
                                    selectedTab = BottomNavTab.EXTRACCION
                                }
                            )

                            BottomNavTab.PROFILE -> ProfileScreen(
                                onBack = { selectedTab = BottomNavTab.EXTRACCION },
                                onLogout = {
                                    authViewModel.logout()
                                    selectedTab = BottomNavTab.EXTRACCION
                                },
                                authRepository = authRepository
                            )

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
