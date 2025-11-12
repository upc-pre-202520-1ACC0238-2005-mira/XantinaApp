package com.upc.xantina.features.conecta.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.core.domain.repository.AuthRepository
import com.upc.xantina.features.social.ui.SocialFeedScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConectaScreen(
    onProfileClick: () -> Unit,
    authRepository: AuthRepository
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4B2E2E))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Conecta", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Comunidad cafetera", color = Color.White, fontSize = 14.sp)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onProfileClick() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Perfil",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Text("Mi Perfil", color = Color.White, fontSize = 12.sp)
            }
        }

        // Feed Social
        SocialFeedScreen(authRepository = authRepository)
    }
}
