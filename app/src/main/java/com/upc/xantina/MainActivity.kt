package com.upc.xantina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.upc.xantina.ui.theme.XantinaAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            XantinaAppTheme {
                val navController = rememberNavController()

                Scaffold { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { HomeScreen() }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Text(
        text = "¡Bienvenido a XantinaApp!",
        style = MaterialTheme.typography.headlineMedium
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    XantinaAppTheme {
        HomeScreen()
    }
}
