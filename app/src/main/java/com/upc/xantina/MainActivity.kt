package com.upc.xantina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.upc.xantina.features.auth.ui.AuthScreen
import com.upc.xantina.ui.theme.XantinaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XantinaTheme {
                AuthScreen(
                    onLoginSuccess = {
                        // TODO: Navegar a la pantalla principal después del login exitoso
                    },
                    onRegisterSuccess = {
                        // TODO: Navegar a la pantalla principal después del registro exitoso
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    XantinaTheme {
        AuthScreen()
    }
}