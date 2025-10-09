package com.upc.xantina.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextSecondary

@Composable
fun XantinaLogo(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Logo con gradiente y sombra más moderna
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = XantinaPrimary,
                    shape = CircleShape
                )
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = XantinaPrimary.copy(alpha = 0.3f),
                    spotColor = XantinaPrimary.copy(alpha = 0.3f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "☕",
                fontSize = 48.sp,
                color = androidx.compose.ui.graphics.Color.White
            )
        }
        
        // Nombre de la aplicación con mejor tipografía
        Text(
            text = "Xantina",
            color = XantinaTextPrimary,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
        
        // Tagline con mejor estilo
        Text(
            text = "Tu compañero de café de especialidad",
            color = XantinaTextSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}
