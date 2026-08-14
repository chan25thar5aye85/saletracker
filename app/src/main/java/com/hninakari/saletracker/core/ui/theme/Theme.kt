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
    Font(R.font.myanmar, weight = FontWeight.Normal),
    Font(R.font.myanmar, weight = FontWeight.Medium),
    Font(R.font.myanmar, weight = FontWeight.Bold)
)

private val PurpleColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryDark,
    secondary = PrimaryLight,
    onSecondary = Color.White,
    secondaryContainer = PrimaryLight.copy(alpha = 0.15f),
    onSecondaryContainer = PrimaryDark,
    tertiary = PrimaryDark,
    onTertiary = Color.White,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = PrimaryLight.copy(alpha = 0.08f),
    onSurfaceVariant = TextSecondary,
    error = Color(0xFFB71C1C),
    onError = Color.White,
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFFB71C1C)
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PurpleColorScheme,
        typography = MaterialTheme.typography.copy(
            displayLarge = MaterialTheme.typography.displayLarge.copy(fontFamily = MyanmarFontFamily),
            displayMedium = MaterialTheme.typography.displayMedium.copy(fontFamily = MyanmarFontFamily),
            displaySmall = MaterialTheme.typography.displaySmall.copy(fontFamily = MyanmarFontFamily),
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontFamily = MyanmarFontFamily),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontFamily = MyanmarFontFamily),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontFamily = MyanmarFontFamily),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = MyanmarFontFamily),
            titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = MyanmarFontFamily),
            titleSmall = MaterialTheme.typography.titleSmall.copy(fontFamily = MyanmarFontFamily),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = MyanmarFontFamily),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = MyanmarFontFamily),
            bodySmall = MaterialTheme.typography.bodySmall.copy(fontFamily = MyanmarFontFamily),
            labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = MyanmarFontFamily),
            labelMedium = MaterialTheme.typography.labelMedium.copy(fontFamily = MyanmarFontFamily),
            labelSmall = MaterialTheme.typography.labelSmall.copy(fontFamily = MyanmarFontFamily)
        ),
        content = content
    )
}
