package com.upc.xantina.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upc.xantina.shared.ui.theme.XantinaBackground
import com.upc.xantina.shared.ui.theme.XantinaPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextPrimary
import com.upc.xantina.shared.ui.theme.XantinaTextSecondary

@Composable
fun XantinaBottomNavigation(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(XantinaBackground)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Tab Extrae
        BottomNavItem(
            icon = Icons.Default.Home,
            label = "Extrae",
            isSelected = selectedTab == BottomNavTab.EXTRACCION,
            onClick = { onTabSelected(BottomNavTab.EXTRACCION) }
        )
        
        // Tab Tienda
        BottomNavItem(
            icon = Icons.Default.ShoppingCart,
            label = "Tienda",
            isSelected = selectedTab == BottomNavTab.TIENDA,
            onClick = { onTabSelected(BottomNavTab.TIENDA) }
        )
        
        // Tab Conecta
        BottomNavItem(
            icon = Icons.Default.Person,
            label = "Conecta",
            isSelected = selectedTab == BottomNavTab.CONECTA,
            onClick = { onTabSelected(BottomNavTab.CONECTA) }
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) XantinaPrimary else XantinaTextSecondary,
            modifier = Modifier.size(24.dp)
        )
        
        Text(
            text = label,
            color = if (isSelected) XantinaPrimary else XantinaTextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

enum class BottomNavTab {
    EXTRACCION, TIENDA, CONECTA
}
