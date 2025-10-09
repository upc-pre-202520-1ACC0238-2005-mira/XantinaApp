package com.upc.xantina.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.shared.ui.theme.XantinaCardBackground
import com.upc.xantina.shared.ui.theme.XantinaTextPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextSecondary

@Composable
fun MethodCard(
    nombre: String,
    descripcion: String,
    tiempoPreparacion: String,
    icono: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = com.upc.xantina.shared.ui.theme.XantinaPrimary.copy(alpha = 0.1f),
                spotColor = com.upc.xantina.shared.ui.theme.XantinaPrimary.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(XantinaCardBackground)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Icono del método
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(com.upc.xantina.shared.ui.theme.XantinaPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getMethodIcon(icono),
                    fontSize = 24.sp
                )
            }

            // Información del método
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nombre,
                    color = XantinaTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = descripcion,
                    color = XantinaTextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        // Lado derecho: Tiempo de preparación
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "🕒",
                fontSize = 16.sp
            )
            Text(
                text = tiempoPreparacion,
                color = XantinaTextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Obtiene el emoji correspondiente al método de extracción
 */
private fun getMethodIcon(icono: String): String {
    return when (icono.lowercase()) {
        "prensa", "french press" -> "🫖"
        "v60", "pour over" -> "⏳"
        "aeropress" -> "🚀"
        "espresso" -> "☕"
        "chemex" -> "🧪"
        "moka" -> "☕"
        "cold brew" -> "❄️"
        else -> "☕"
    }
}
