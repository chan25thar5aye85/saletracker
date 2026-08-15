package com.hninakari.saletracker.core.ui.theme

import androidx.compose.material3.darkColorScheme

// ============================================================
// BLUE SOLARIZED THEME
// ============================================================

val DarkColorScheme = darkColorScheme(
    primary = SolarizedBlue,
    onPrimary = Base3,

    primaryContainer = SolarizedBlue.copy(alpha = 0.20f),
    onPrimaryContainer = Base3,

    secondary = SolarizedCyan,
    onSecondary = Base3,

    secondaryContainer = SolarizedCyan.copy(alpha = 0.15f),
    onSecondaryContainer = Base3,

    tertiary = SolarizedViolet,
    onTertiary = Base3,

    // Main screen
    background = Base02,
    onBackground = Base3,

    // Cards
    surface = Base01,
    onSurface = Base3,

    surfaceVariant = Base01,
    onSurfaceVariant = Base1,

    error = SolarizedRed,
    onError = Base3
)

val DarkAppColors = AppColors(
    // Status bar + Top bar + Navigation bar
    barBackground = Base03,
    barContent = Base3
)
