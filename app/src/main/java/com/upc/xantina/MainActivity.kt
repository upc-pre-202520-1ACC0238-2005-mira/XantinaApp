package com.upc.xantina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.upc.xantina.features.auth.ui.AuthScreen
import com.upc.xantina.features.extraccion.ui.ExtraccionScreen
import com.upc.xantina.ui.theme.XantinaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XantinaTheme {
                var isLoggedIn by remember { mutableStateOf(false) }
                
                if (isLoggedIn) {
                    ExtraccionScreen(
                        onNavigateToCreate = {
                            // TODO: Navegar a pantalla de crear extracción
                        },
                        onNavigateToAll = {
                            // TODO: Navegar a pantalla de todas las extracciones
                        },
                    )
                } else {
                    AuthScreen(
                        onLoginSuccess = {
                            isLoggedIn = true
                        },
                        onRegisterSuccess = {
                            isLoggedIn = true
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExtraccionScreenPreview() {
    XantinaTheme {
        ExtraccionScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    XantinaTheme {
        AuthScreen()
    }
}