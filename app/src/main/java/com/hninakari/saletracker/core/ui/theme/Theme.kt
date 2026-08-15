package com.hninakari.saletracker.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.hninakari.saletracker.R
import com.hninakari.saletracker.core.UserPreferences

// ============================================================
// MYANMAR FONT
// ============================================================

val MyanmarFontFamily = FontFamily(
    Font(R.font.myanmar, FontWeight.Normal),
    Font(R.font.myanmar, FontWeight.Medium),
    Font(R.font.myanmar, FontWeight.Bold)
)

// ============================================================
// APP-SPECIFIC THEME COLORS
// ============================================================

@Immutable
data class AppColors(
    val barBackground: Color,
    val barContent: Color
)

private val LocalAppColors = staticCompositionLocalOf {
    AppColors(
        barBackground = Color.Unspecified,
        barContent = Color.Unspecified
    )
}

object AppThemeColors {

    val colors: AppColors
        @Composable
        get() = LocalAppColors.current
}

// ============================================================
// APP THEME
// ============================================================

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val userPreferences = remember {
        UserPreferences.getInstance(context)
    }

    val themeMode by userPreferences.themeMode.collectAsState()

    // ---------------------------------------------------------
    // ONE THEME SELECTOR
    // ---------------------------------------------------------

    val (colorScheme, appColors) = when (themeMode) {

        "purple" -> {
            PurpleColorScheme to PurpleAppColors
        }

        "green" -> {
            GreenColorScheme to GreenAppColors
        }

        else -> {
            DarkColorScheme to DarkAppColors
        }
    }

    // ---------------------------------------------------------
    // PROVIDE THE SELECTED THEME TO THE ENTIRE APP
    // ---------------------------------------------------------

    CompositionLocalProvider(
        LocalAppColors provides appColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,

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
}
