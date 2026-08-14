package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import com.hninakari.saletracker.core.ui.theme.Primary
import com.hninakari.saletracker.core.ui.theme.TextPrimary

private fun Modifier.uniformCard(): Modifier = this
    .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp), clip = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSettingsScreen(
    currentUserId: String,
    onSaveUserId: (String) -> Unit,
    onBack: () -> Unit
) {
    var userId by remember { mutableStateOf(currentUserId) }
    var showSaveSuccess by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "⚙️ Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // User ID Card
            Card(
                modifier = Modifier.fillMaxWidth().uniformCard(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "👤 User ID",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    
                    Text(
                        text = "Devices with the same User ID will share data",
                        fontSize = 12.sp,
                        color = TextPrimary.copy(alpha = 0.5f),
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )
                    
                    OutlinedTextField(
                        value = userId,
                        onValueChange = { 
                            userId = it.trim()
                            showSaveSuccess = false
                        },
                        label = { Text("Enter User ID") },
                        placeholder = { Text("e.g., my-family") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = TextPrimary.copy(alpha = 0.3f)
                        )
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (userId.isNotBlank()) {
                                    onSaveUserId(userId)
                                    showSaveSuccess = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary
                            )
                        ) {
                            Text("Save User ID")
                        }
                        
                        OutlinedButton(
                            onClick = {
                                userId = "default-user"
                                showSaveSuccess = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reset")
                        }
                    }
                    
                    if (showSaveSuccess) {
                        Text(
                            text = "✅ User ID saved: $userId",
                            color = Primary,
                            modifier = Modifier.padding(top = 8.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            // Current Status Card
            Card(
                modifier = Modifier.fillMaxWidth().uniformCard(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "📊 Current Status",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "User ID:",
                            color = TextPrimary.copy(alpha = 0.6f)
                        )
                        Text(
                            currentUserId.ifEmpty { "Not set" },
                            color = if (currentUserId.isNotEmpty() && currentUserId != "default-user") 
                                Primary 
                            else 
                                TextPrimary.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Status:",
                            color = TextPrimary.copy(alpha = 0.6f)
                        )
                        Text(
                            if (currentUserId.isNotEmpty() && currentUserId != "default-user") 
                                "✅ Configured" 
                            else 
                                "⚠️ Using default",
                            color = if (currentUserId.isNotEmpty() && currentUserId != "default-user") 
                                Primary 
                            else 
                                TextPrimary.copy(alpha = 0.5f)
                        )
                    }
                }
            }
            
            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth().uniformCard(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ℹ️ How to share data between devices",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "1. Set the same User ID on all devices\n2. Press Sync on each device\n3. Data will be shared automatically",
                        fontSize = 12.sp,
                        color = TextPrimary.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
