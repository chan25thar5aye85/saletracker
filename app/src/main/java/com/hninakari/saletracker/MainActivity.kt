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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.hninakari.saletracker.core.ui.theme.AppTheme
import com.hninakari.saletracker.core.ui.theme.AppThemeColors
import com.hninakari.saletracker.ui.navigation.AppNavigation
import com.hninakari.saletracker.utils.LanguageManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // -----------------------------------------------------
        // Edge-to-edge
        // -----------------------------------------------------

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            )
        }

        // -----------------------------------------------------
        // Language
        // -----------------------------------------------------

        val languageCode = LanguageManager.getLanguage(this)
        LanguageManager.setLocale(this, languageCode)

        // -----------------------------------------------------
        // Application / repositories
        // -----------------------------------------------------

        val application = application as SaleTrackerApplication

        // -----------------------------------------------------
        // Compose
        // -----------------------------------------------------

        setContent {

            AppTheme {

                // System bars use the SAME color as
                // MaterialTheme.colorScheme.background
                SystemBarsUpdater()

                Surface(
                    modifier = Modifier.fillMaxSize(),

                    // IMPORTANT:
                    // Do NOT use the old Background from Color.kt.
                    // Use the currently selected theme.
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
// SYSTEM BARS
// ============================================================

@Composable
fun SystemBarsUpdater() {

    val view = LocalView.current

    // ---------------------------------------------------------
    // Use the currently selected Material theme colors.
    //
    // Therefore:
    //
    // Light  -> Light background
    // Dark   -> Dark background
    // Purple -> Purple background
    // Green  -> Green background
    // ---------------------------------------------------------

    val appColors = AppThemeColors.colors
    val barColor = appColors.barBackground
    val contentColor = appColors.barContent

    SideEffect {

        val window = (view.context as? ComponentActivity)?.window

        if (window != null) {

            val color = barColor.toArgb()

            // Status bar
            window.statusBarColor = color

            // Navigation bar
            window.navigationBarColor = color

            // -------------------------------------------------
            // System bar icon color
            //
            // Dark background -> white icons
            // Light background -> dark icons
            // -------------------------------------------------

            val controller = WindowCompat.getInsetsController(
                window,
                view
            )

            val useDarkIcons = contentColor.luminance() > 0.5f

            controller.isAppearanceLightStatusBars = useDarkIcons
            controller.isAppearanceLightNavigationBars = useDarkIcons
        }
    }
}
