package com.hninakari.saletracker

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.hninakari.saletracker.core.UserPreferences
import com.hninakari.saletracker.core.ui.theme.AppTheme
import com.hninakari.saletracker.core.ui.theme.AppThemeColors
import com.hninakari.saletracker.ui.navigation.AppNavigation
import com.hninakari.saletracker.utils.LanguageManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            )
        }

        // Language
        val languageCode = LanguageManager.getLanguage(this)
        LanguageManager.setLocale(this, languageCode)

        // Application / repositories
        val application = application as SaleTrackerApplication

        // Compose
        setContent {
            AppTheme {
                SystemBarsUpdater()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        saleRepository = application.saleRepository,
                        expenseRepository = application.expenseRepository,
                        transferRepository = application.transferRepository,
                        personRepository = application.personRepository,
                        debtRepository = application.debtRepository,
                        productRepository = application.productRepository,
                        toBuyRepository = application.toBuyRepository,
                        productSupplierRepository = application.productSupplierRepository,
                        orderRepository = application.orderRepository
                    )
                }
            }
        }
    }
}

// ============================================================
// SYSTEM BARS - THEME BASED HARDCODED
// ============================================================

@Composable
fun SystemBarsUpdater() {

    val view = LocalView.current
    val context = LocalContext.current
    val appColors = AppThemeColors.colors
    val barColor = appColors.barBackground
    
    // Get current theme from UserPreferences
    val userPrefs = remember { UserPreferences.getInstance(context) }
    val currentTheme by userPrefs.themeMode.collectAsState()

    SideEffect {

        val window = (view.context as? ComponentActivity)?.window

        if (window != null) {

            // Set status bar to match the app's bar color
            val color = barColor.toArgb()
            window.statusBarColor = color
            window.navigationBarColor = color

            // -------------------------------------------------
            // THEME BASED STATUS BAR ICON COLOR
            // -------------------------------------------------

            val controller = WindowCompat.getInsetsController(
                window,
                view
            )

            // true = dark icons (black), false = light icons (white)
            val useDarkIcons = when (currentTheme) {
                "purple" -> true   // Purple theme → Dark/Black icons
                "green" -> true    // Green theme → Dark/Black icons
                "dark" -> false    // Dark theme → White icons
                "light" -> true    // Light theme → Dark/Black icons
                else -> true       // Default → Dark/Black icons
            }

            controller.isAppearanceLightStatusBars = useDarkIcons
            controller.isAppearanceLightNavigationBars = useDarkIcons
        }
    }
}
