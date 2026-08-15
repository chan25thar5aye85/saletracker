package com.hninakari.saletracker.core.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ============================================================
// GREEN LIGHT THEME
// ============================================================

private val GreenPrimary = Color(0xFF2E7D32)
private val GreenOnPrimary = Color.White

private val GreenPrimaryContainer = Color(0xFFC8E6C9)
private val GreenOnPrimaryContainer = Color(0xFF123014)

private val GreenSecondary = Color(0xFF43A047)
private val GreenOnSecondary = Color.White

private val GreenSecondaryContainer = Color(0xFFE8F5E9)
private val GreenOnSecondaryContainer = Color(0xFF1B5E20)

private val GreenTertiary = Color(0xFF388E3C)

// Layer colors
private val GreenBar = Color(0xFF1B5E20)
private val GreenBackground = Color(0xFFF1F8F1)
private val GreenSurface = Color(0xFFDDEEDD)
private val GreenSurfaceVariant = Color(0xFFC8DFC8)

private val GreenText = Color(0xFF123014)
private val GreenTextSecondary = Color(0xFF2E7D32)

val GreenColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = GreenOnPrimary,

    primaryContainer = GreenPrimaryContainer,
    onPrimaryContainer = GreenOnPrimaryContainer,

    secondary = GreenSecondary,
    onSecondary = GreenOnSecondary,

    secondaryContainer = GreenSecondaryContainer,
    onSecondaryContainer = GreenOnSecondaryContainer,

    tertiary = GreenTertiary,
    onTertiary = Color.White,

    background = GreenBackground,
    onBackground = GreenText,

    surface = GreenSurface,
    onSurface = GreenText,

    surfaceVariant = GreenSurfaceVariant,
    onSurfaceVariant = GreenTextSecondary,

    error = SolarizedRed,
    onError = Color.White
)

val GreenAppColors = AppColors(
    barBackground = GreenBar,
    barContent = Color.White
)
