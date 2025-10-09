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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.shared.ui.theme.XantinaCardBackground
import com.upc.xantina.shared.ui.theme.XantinaTextPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextSecondary

@Composable
fun RecentCard(
    nombreCafe: String,
    metodoExtraccion: String,
    fechaHora: String,
    calificacion: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(XantinaCardBackground)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            // Nombre del café
            Text(
                text = nombreCafe,
                color = XantinaTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Método y fecha
            Text(
                text = "$metodoExtraccion • $fechaHora",
                color = XantinaTextSecondary,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Calificación con estrellas
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    if (index < calificacion) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Estrella ${index + 1}",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.padding(end = 2.dp)
                        )
                    } else {
                        Text(
                            text = "☆",
                            color = XantinaTextSecondary.copy(alpha = 0.3f),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StarRating(
    rating: Int,
    maxRating: Int = 5,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxRating) { index ->
            if (index < rating) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Estrella ${index + 1}",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.padding(end = 2.dp)
                )
            } else {
                Text(
                    text = "☆",
                    color = XantinaTextSecondary.copy(alpha = 0.3f),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 2.dp)
                )
            }
        }
    }
}
