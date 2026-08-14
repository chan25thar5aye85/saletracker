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
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Drawer content
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(320.dp)
                    .background(MaterialTheme.colorScheme.surface)
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
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "User: $currentUserId",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Divider()
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Drawer Items
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigate("settings")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Sync, contentDescription = "Sync Now") },
                    label = { Text("Sync Now") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigate("sync")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Divider()
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Version 1.0.0",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        content = content
    )
}
