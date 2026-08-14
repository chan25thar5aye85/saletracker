package com.hninakari.saletracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.hninakari.saletracker.core.ui.theme.AppTheme
import com.hninakari.saletracker.core.ui.theme.Background
import com.hninakari.saletracker.data.repository.*
import com.hninakari.saletracker.ui.navigation.AppNavigation
import com.hninakari.saletracker.utils.LanguageManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val languageCode = LanguageManager.getLanguage(this)
        LanguageManager.setLocale(this, languageCode)
        
        val application = application as SaleTrackerApplication
        
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
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

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val context = LocalContext.current
    AppTheme {
        AppNavigation(
            saleRepository = SaleRepository(context),
            expenseRepository = ExpenseRepository(context),
            transferRepository = TransferRepository(context),
            personRepository = PersonRepository(context),
            debtRepository = DebtRepository(context),
            productRepository = ProductRepository(context),
            toBuyRepository = ToBuyRepository(context),
            productSupplierRepository = ProductSupplierRepository(context),
            orderRepository = OrderRepository(context)
        )
    }
}
