package com.hninakari.saletracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.core.ui.theme.AppThemeColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
    drawerState: DrawerState,
    scope: CoroutineScope,
    currentUserId: String,
    onNavigate: (String) -> Unit,
    content: @Composable () -> Unit
) {
    val appColors = AppThemeColors.colors

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(320.dp)
                    .background(appColors.barBackground)
                    .padding(16.dp)
            ) {
                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SaleTracker",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.barContent
                    )

                    Text(
                        text = "User: $currentUserId",
                        fontSize = 12.sp,
                        color = appColors.barContent.copy(alpha = 0.75f)
                    )
                }

                HorizontalDivider(
                    color = appColors.barContent.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            tint = appColors.barContent
                        )
                    },
                    label = {
                        Text(
                            "Settings",
                            color = appColors.barContent
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigate("settings")
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = appColors.barBackground,
                        unselectedIconColor = appColors.barContent,
                        unselectedTextColor = appColors.barContent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = null,
                            tint = appColors.barContent
                        )
                    },
                    label = {
                        Text(
                            "Sync Now",
                            color = appColors.barContent
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigate("sync")
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = appColors.barBackground,
                        unselectedIconColor = appColors.barContent,
                        unselectedTextColor = appColors.barContent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                HorizontalDivider(
                    color = appColors.barContent.copy(alpha = 0.15f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Version 1.0.0",
                        fontSize = 11.sp,
                        color = appColors.barContent.copy(alpha = 0.7f)
                    )
                }
            }
        },
        content = content
    )
}
