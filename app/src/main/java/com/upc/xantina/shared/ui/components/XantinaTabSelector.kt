package com.upc.xantina.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.shared.ui.theme.XantinaCardBackground
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import com.upc.xantina.shared.ui.theme.XantinaSecondary
import com.upc.xantina.shared.ui.theme.XantinaTextPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextSecondary

@Composable
fun XantinaTabSelector(
    selectedTab: AuthTab,
    onTabSelected: (AuthTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(XantinaCardBackground)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Tab de Login
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (selectedTab == AuthTab.LOGIN) XantinaPrimary else androidx.compose.ui.graphics.Color.Transparent
                    )
                    .clickable { onTabSelected(AuthTab.LOGIN) }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Iniciar Sesión",
                    color = if (selectedTab == AuthTab.LOGIN) androidx.compose.ui.graphics.Color.White else XantinaTextSecondary,
                    fontSize = 16.sp,
                    fontWeight = if (selectedTab == AuthTab.LOGIN) FontWeight.SemiBold else FontWeight.Medium
                )
            }
            
            // Tab de Register
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (selectedTab == AuthTab.REGISTER) XantinaPrimary else androidx.compose.ui.graphics.Color.Transparent
                    )
                    .clickable { onTabSelected(AuthTab.REGISTER) }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Registrarse",
                    color = if (selectedTab == AuthTab.REGISTER) androidx.compose.ui.graphics.Color.White else XantinaTextSecondary,
                    fontSize = 16.sp,
                    fontWeight = if (selectedTab == AuthTab.REGISTER) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}

enum class AuthTab {
    LOGIN, REGISTER
}
