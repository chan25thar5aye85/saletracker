package com.hninakari.saletracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, contentDescription = "Add Sale") },
            label = { Text(stringResource(R.string.add_sale), fontSize = 11.sp) },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Remove, contentDescription = "Add Expense") },
            label = { Text(stringResource(R.string.add_expense), fontSize = 11.sp) },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Add Transfer") },
            label = { Text(stringResource(R.string.add_transfer), fontSize = 11.sp) },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.People, contentDescription = "People") },
            label = { Text(stringResource(R.string.people), fontSize = 11.sp) },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "To Buy") },
            label = { Text("To Buy", fontSize = 11.sp) },
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) }
        )
    }
}
