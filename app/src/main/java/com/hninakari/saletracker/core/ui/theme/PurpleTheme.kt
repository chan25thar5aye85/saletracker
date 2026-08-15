package com.hninakari.saletracker.core.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ============================================================
// VIOLET LIGHT THEME
// ============================================================

// Main violet
private val PurplePrimary = Color(0xFF7B1FA2)
private val PurpleOnPrimary = Color.White

private val PurplePrimaryContainer = Color(0xFFE8D5EE)
private val PurpleOnPrimaryContainer = Color(0xFF4A145F)

// Secondary violet
private val PurpleSecondary = Color(0xFF9C4DB5)
private val PurpleOnSecondary = Color.White

private val PurpleSecondaryContainer = Color(0xFFF0DDF3)
private val PurpleOnSecondaryContainer = Color(0xFF54205F)

// Tertiary
private val PurpleTertiary = Color(0xFFAB47BC)

// ============================================================
// BACKGROUND / SURFACE
// ============================================================

// Soft creamy violet background
private val PurpleBackground = Color(0xFFF8F0FA)

// Card surface
private val PurpleSurface = Color(0xFFF1E3F4)

// Slightly deeper surface
private val PurpleSurfaceVariant = Color(0xFFE5D0E9)

// ============================================================
// TEXT
// ============================================================

private val PurpleText = Color(0xFF35183D)
private val PurpleTextSecondary = Color(0xFF70457A)

// ============================================================
// TOP BAR
// ============================================================

private val PurpleBar = Color(0xFF7B1FA2)

// ============================================================
// MATERIAL COLOR SCHEME
// ============================================================

val PurpleColorScheme = lightColorScheme(

    primary = PurplePrimary,
    onPrimary = PurpleOnPrimary,

    primaryContainer = PurplePrimaryContainer,
    onPrimaryContainer = PurpleOnPrimaryContainer,

    secondary = PurpleSecondary,
    onSecondary = PurpleOnSecondary,

    secondaryContainer = PurpleSecondaryContainer,
    onSecondaryContainer = PurpleOnSecondaryContainer,

    tertiary = PurpleTertiary,
    onTertiary = Color.White,

    background = PurpleBackground,
    onBackground = PurpleText,

    surface = PurpleSurface,
    onSurface = PurpleText,

    surfaceVariant = PurpleSurfaceVariant,
    onSurfaceVariant = PurpleTextSecondary,

    error = SolarizedRed,
    onError = Color.White
)

// ============================================================
// APP-SPECIFIC COLORS
// ============================================================

val PurpleAppColors = AppColors(
    barBackground = PurpleBar,
    barContent = Color.White
)
