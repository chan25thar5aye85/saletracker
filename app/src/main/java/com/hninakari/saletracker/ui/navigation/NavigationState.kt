package com.hninakari.saletracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.hninakari.saletracker.data.model.Person
import com.hninakari.saletracker.data.model.Product
import com.hninakari.saletracker.data.model.Sale
import com.hninakari.saletracker.data.model.Expense
import com.hninakari.saletracker.data.model.Transfer

class NavigationState {
    // Main navigation
    val selectedTab = mutableStateOf(0)
    val currentScreen = mutableStateOf("main")
    
    // Settings
    val showSettings = mutableStateOf(false)
    
    // Dialogs
    val showLanguageDialog = mutableStateOf(false)
    val showAddPersonDialog = mutableStateOf(false)
    val showAddDebtDialog = mutableStateOf(false)
    val showPaymentDialog = mutableStateOf(false)
    val showAddToBuyItemDialog = mutableStateOf(false)
    val showMarkAsBoughtDialog = mutableStateOf(false)
    val showNewOrderDialog = mutableStateOf(false)
    val showAddProductDialog = mutableStateOf(false)
    val showFilterDropdown = mutableStateOf(false)
    
    // Success dialogs
    val showSaleSuccess = mutableStateOf(false)
    val showExpenseSuccess = mutableStateOf(false)
    val showTransferSuccess = mutableStateOf(false)
    
    // Screens
    val showOrderHistory = mutableStateOf(false)
    val showOrderList = mutableStateOf(false)
    val showPurchaseHistory = mutableStateOf(false)
    val showToBuyScreen = mutableStateOf(false)
    
    // Selected items
    val selectedPerson = mutableStateOf<Person?>(null)
    val selectedProduct = mutableStateOf<Product?>(null)
    val selectedDebtId = mutableStateOf<Int?>(null)
    val selectedOrderId = mutableStateOf<Int?>(null)
    val selectedToBuyItemIds = mutableStateOf<List<Int>>(emptyList())
    
    // Last added items for success dialogs
    val lastSale = mutableStateOf<Sale?>(null)
    val lastExpense = mutableStateOf<Expense?>(null)
    val lastTransfer = mutableStateOf<Transfer?>(null)
}

@Composable
fun rememberNavigationState(): NavigationState {
    return remember { NavigationState() }
}
