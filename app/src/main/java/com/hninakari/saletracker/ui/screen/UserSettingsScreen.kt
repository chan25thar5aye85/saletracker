package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.core.ui.theme.AppThemeColors

private fun Modifier.uniformCard(): Modifier =
    this.shadow(
        elevation = 3.dp,
        shape = RoundedCornerShape(12.dp),
        clip = false
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSettingsScreen(
    currentUserId: String,
    currentTheme: String,
    onSaveUserId: (String) -> Unit,
    onThemeChange: (String) -> Unit,
    onBack: () -> Unit
) {
    var userId by remember(currentUserId) {
        mutableStateOf(currentUserId)
    }

    var showSaveSuccess by remember {
        mutableStateOf(false)
    }

    val appColors = AppThemeColors.colors

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "⚙️ Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.barContent
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = appColors.barContent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appColors.barBackground,
                    titleContentColor = appColors.barContent,
                    navigationIconContentColor = appColors.barContent
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ====================================================
            // THEME CARD
            // ====================================================

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .uniformCard(),
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
                        text = "🎨 Theme",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Choose your color theme",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            top = 4.dp,
                            bottom = 12.dp
                        )
                    )

                    // =================================================
                    // THEME SELECTOR
                    // =================================================

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        ThemeButton(
                            label = "Dark",
                            icon = Icons.Default.DarkMode,
                            color = Color(0xFF073642),
                            isSelected = currentTheme == "dark",
                            onClick = {
                                onThemeChange("dark")
                            },
                            modifier = Modifier.weight(1f)
                        )

                        ThemeButton(
                            label = "Purple",
                            icon = Icons.Default.Palette,
                            color = Color(0xFF7B1FA2),
                            isSelected = currentTheme == "purple",
                            onClick = {
                                onThemeChange("purple")
                            },
                            modifier = Modifier.weight(1f)
                        )

                        ThemeButton(
                            label = "Green",
                            icon = Icons.Default.Park,
                            color = Color(0xFF2E7D32),
                            isSelected = currentTheme == "green",
                            onClick = {
                                onThemeChange("green")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        text = "Current: ${
                            when (currentTheme) {
                                "dark" -> "Dark Solarized"
                                "purple" -> "Purple"
                                "green" -> "Green"
                                else -> "Dark Solarized"
                            }
                        }",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }

            // ====================================================
            // USER ID CARD
            // ====================================================

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .uniformCard(),
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
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Devices with the same User ID will share data",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            top = 4.dp,
                            bottom = 12.dp
                        )
                    )

                    OutlinedTextField(
                        value = userId,
                        onValueChange = {
                            userId = it.trim()
                            showSaveSuccess = false
                        },
                        label = {
                            Text("Enter User ID")
                        },
                        placeholder = {
                            Text("e.g., my-family")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor =
                                MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor =
                                MaterialTheme.colorScheme.onSurface
                                    .copy(alpha = 0.3f)
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
                                containerColor =
                                    MaterialTheme.colorScheme.primary,
                                contentColor =
                                    MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(4.dp)
                            )

                            Text("Save User ID")
                        }

                        OutlinedButton(
                            onClick = {
                                userId = "default-user"
                                showSaveSuccess = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor =
                                    MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(4.dp)
                            )

                            Text("Reset")
                        }
                    }

                    if (showSaveSuccess) {
                        Text(
                            text = "✅ User ID saved: $userId",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // ====================================================
            // CURRENT STATUS CARD
            // ====================================================

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .uniformCard(),
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
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "User ID:",
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = currentUserId.ifEmpty {
                                "Not set"
                            },
                            color =
                                if (
                                    currentUserId.isNotEmpty() &&
                                    currentUserId != "default-user"
                                ) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                                },
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Theme:",
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = when (currentTheme) {
                                "dark" -> "Dark Solarized"
                                "purple" -> "Purple"
                                "green" -> "Green"
                                else -> "Dark Solarized"
                            },
                            fontWeight = FontWeight.Medium,
                            color =
                                MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Status:",
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text =
                                if (
                                    currentUserId.isNotEmpty() &&
                                    currentUserId != "default-user"
                                ) {
                                    "✅ Configured"
                                } else {
                                    "⚠️ Using default"
                                },
                            color =
                                if (
                                    currentUserId.isNotEmpty() &&
                                    currentUserId != "default-user"
                                ) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                                }
                        )
                    }
                }
            }

            // ====================================================
            // INFO CARD
            // ====================================================

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .uniformCard(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceVariant
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
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text =
                            "1. Set the same User ID on all devices\n" +
                            "2. Press Sync on each device\n" +
                            "3. Data will be shared automatically",
                        fontSize = 12.sp,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ================================================================
// THEME BUTTON
// ================================================================

@Composable
fun ThemeButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(
            horizontal = 6.dp,
            vertical = 8.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) {
                color
            } else {
                color.copy(alpha = 0.75f)
            },
            contentColor = Color.White
        )
    ) {

        Row(
            horizontalArrangement =
                Arrangement.spacedBy(3.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )

            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}
