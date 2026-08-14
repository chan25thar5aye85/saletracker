package com.hninakari.saletracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.R

@Composable
fun AppBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 0.dp
    ) {
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.secondary,
            selectedTextColor = MaterialTheme.colorScheme.secondary,
            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, null) },
            label = { Text(stringResource(R.string.add_sale), fontSize = 11.sp) },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            colors = itemColors
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Remove, null) },
            label = { Text(stringResource(R.string.add_expense), fontSize = 11.sp) },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            colors = itemColors
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.SwapHoriz, null) },
            label = { Text(stringResource(R.string.add_transfer), fontSize = 11.sp) },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            colors = itemColors
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.People, null) },
            label = { Text(stringResource(R.string.people), fontSize = 11.sp) },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            colors = itemColors
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, null) },
            label = { Text("To Buy", fontSize = 11.sp) },
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) },
            colors = itemColors
        )
    }
}
