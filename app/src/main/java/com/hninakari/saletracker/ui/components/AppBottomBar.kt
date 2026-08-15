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
    val backgroundColor = MaterialTheme.colorScheme.background
    val contentColor = MaterialTheme.colorScheme.onBackground

    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = contentColor,
        selectedTextColor = contentColor,
        indicatorColor = backgroundColor,
        unselectedIconColor = contentColor.copy(alpha = 0.65f),
        unselectedTextColor = contentColor.copy(alpha = 0.65f)
    )

    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {

        NavigationBar(
            containerColor = backgroundColor,
            contentColor = contentColor,
            tonalElevation = 0.dp
        ) {

            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        stringResource(R.string.add_sale),
                        fontSize = 11.sp
                    )
                },
                selected = selectedTab == 0,
                onClick = {
                    onTabSelected(0)
                },
                colors = itemColors
            )

            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        stringResource(R.string.add_expense),
                        fontSize = 11.sp
                    )
                },
                selected = selectedTab == 1,
                onClick = {
                    onTabSelected(1)
                },
                colors = itemColors
            )

            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        stringResource(R.string.add_transfer),
                        fontSize = 11.sp
                    )
                },
                selected = selectedTab == 2,
                onClick = {
                    onTabSelected(2)
                },
                colors = itemColors
            )

            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Default.People,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        stringResource(R.string.people),
                        fontSize = 11.sp
                    )
                },
                selected = selectedTab == 3,
                onClick = {
                    onTabSelected(3)
                },
                colors = itemColors
            )

            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        "To Buy",
                        fontSize = 11.sp
                    )
                },
                selected = selectedTab == 4,
                onClick = {
                    onTabSelected(4)
                },
                colors = itemColors
            )
        }
    }
}
