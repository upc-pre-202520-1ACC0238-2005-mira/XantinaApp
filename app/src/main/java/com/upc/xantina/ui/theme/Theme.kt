package com.upc.xantina.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.upc.xantina.shared.ui.theme.XantinaBackground
import com.upc.xantina.shared.ui.theme.XantinaCardBackground
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import com.upc.xantina.shared.ui.theme.XantinaSecondary
import com.upc.xantina.shared.ui.theme.XantinaTextPrimary

private val DarkColorScheme = darkColorScheme(
    primary = XantinaPrimary,
    secondary = XantinaSecondary,
    tertiary = XantinaPrimary,
    background = XantinaBackground,
    surface = XantinaCardBackground,
    onPrimary = Color.White,
    onSecondary = XantinaTextPrimary,
    onTertiary = Color.White,
    onBackground = XantinaTextPrimary,
    onSurface = XantinaTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = XantinaPrimary,
    secondary = XantinaSecondary,
    tertiary = XantinaPrimary,
    background = XantinaBackground,
    surface = XantinaCardBackground,
    onPrimary = Color.White,
    onSecondary = XantinaTextPrimary,
    onTertiary = Color.White,
    onBackground = XantinaTextPrimary,
    onSurface = XantinaTextPrimary
)

@Composable
fun XantinaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}