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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
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
            // Icono del método (imagen real)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(com.upc.xantina.shared.ui.theme.XantinaPrimary.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(getMethodImageUrl(icono))
                        .crossfade(true)
                        .build(),
                    contentDescription = nombre,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit,
                    loading = {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = com.upc.xantina.shared.ui.theme.XantinaPrimary
                        )
                    },
                    error = {
                        Text(
                            text = getMethodEmoji(icono),
                            fontSize = 32.sp
                        )
                    }
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
 * Obtiene la URL de la imagen real del método de extracción
 */
private fun getMethodImageUrl(icono: String): String {
    return when (icono.lowercase()) {
        "aeropress" -> "https://exploracafe.pe/cdn/shop/files/1.webp?v=1755203368"
        "chemex" -> "https://alboradacafe.pe/cdn/shop/products/chemex-classic-8cup-detail_1.png?v=1659342030"
        "prensa", "prensa francesa", "prensa_francesa", "french press" -> "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSWx02O0Ks_lD9H1OSLbPorgFhJNzcaoqAmOA&s"
        "v60", "pour over" -> "https://alboradacafe.pe/cdn/shop/products/2141018.jpg?v=1659338700"
        else -> "https://alboradacafe.pe/cdn/shop/products/2141018.jpg?v=1659338700" // Default V60
    }
}

/**
 * Obtiene el emoji de respaldo para el método
 */
private fun getMethodEmoji(icono: String): String {
    return when (icono.lowercase()) {
        "aeropress" -> "☕"
        "chemex" -> "⚗️"
        "prensa", "prensa francesa", "prensa_francesa", "french press" -> "🫖"
        "v60", "pour over" -> "☕"
        else -> "☕"
    }
}
