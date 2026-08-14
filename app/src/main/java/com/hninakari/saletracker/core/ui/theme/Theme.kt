package com.hninakari.saletracker.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.hninakari.saletracker.R

val MyanmarFontFamily = FontFamily(
    Font(R.font.myanmar, FontWeight.Normal),
    Font(R.font.myanmar, FontWeight.Medium),
    Font(R.font.myanmar, FontWeight.Bold)
)

private val SolarizedColorScheme = lightColorScheme(

    // ------------------------------------------------------------
    // Primary actions
    // ------------------------------------------------------------

    primary = Primary,
    onPrimary = Color.White,

    primaryContainer = Green.copy(alpha = 0.15f),
    onPrimaryContainer = Base02,

    // ------------------------------------------------------------
    // Secondary actions
    // ------------------------------------------------------------

    secondary = Cyan,
    onSecondary = Color.White,

    secondaryContainer = Cyan.copy(alpha = 0.15f),
    onSecondaryContainer = Base02,

    // ------------------------------------------------------------
    // Tertiary
    // ------------------------------------------------------------

    tertiary = Blue,
    onTertiary = Color.White,

    // ------------------------------------------------------------
    // Main screen
    // ------------------------------------------------------------

    background = Background,
    onBackground = TextPrimary,

    // ------------------------------------------------------------
    // Cards / dialogs
    // ------------------------------------------------------------

    surface = Surface,
    onSurface = TextPrimary,

    // ------------------------------------------------------------
    // Top / bottom bars
    // ------------------------------------------------------------

    surfaceContainer = SurfaceBar,
    onSurfaceVariant = Color.White,

    // ------------------------------------------------------------
    // Error
    // ------------------------------------------------------------

    error = Red,
    onError = Color.White,

    errorContainer = Red.copy(alpha = 0.12f),
    onErrorContainer = Red
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SolarizedColorScheme,

        typography = MaterialTheme.typography.copy(

            displayLarge = MaterialTheme.typography.displayLarge.copy(
                fontFamily = MyanmarFontFamily
            ),
            displayMedium = MaterialTheme.typography.displayMedium.copy(
                fontFamily = MyanmarFontFamily
            ),
            displaySmall = MaterialTheme.typography.displaySmall.copy(
                fontFamily = MyanmarFontFamily
            ),

            headlineLarge = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = MyanmarFontFamily
            ),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(
                fontFamily = MyanmarFontFamily
            ),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = MyanmarFontFamily
            ),

            titleLarge = MaterialTheme.typography.titleLarge.copy(
                fontFamily = MyanmarFontFamily
            ),
            titleMedium = MaterialTheme.typography.titleMedium.copy(
                fontFamily = MyanmarFontFamily
            ),
            titleSmall = MaterialTheme.typography.titleSmall.copy(
                fontFamily = MyanmarFontFamily
            ),

            bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = MyanmarFontFamily
            ),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = MyanmarFontFamily
            ),
            bodySmall = MaterialTheme.typography.bodySmall.copy(
                fontFamily = MyanmarFontFamily
            ),

            labelLarge = MaterialTheme.typography.labelLarge.copy(
                fontFamily = MyanmarFontFamily
            ),
            labelMedium = MaterialTheme.typography.labelMedium.copy(
                fontFamily = MyanmarFontFamily
            ),
            labelSmall = MaterialTheme.typography.labelSmall.copy(
                fontFamily = MyanmarFontFamily
            )
        ),

        content = content
    )
}
