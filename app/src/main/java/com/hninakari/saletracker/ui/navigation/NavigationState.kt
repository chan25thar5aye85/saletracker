package com.hninakari.saletracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.hninakari.saletracker.data.model.Person
import com.hninakari.saletracker.data.model.Product
import com.hninakari.saletracker.data.model.Sale
import com.hninakari.saletracker.data.model.Transfer
import com.hninakari.saletracker.data.model.Expense

class NavigationState {
    val selectedTab = mutableStateOf(0)
    val currentScreen = mutableStateOf("main")
    
    // Person detail
    val selectedPerson = mutableStateOf<Person?>(null)
    
    // Debt
    val selectedDebtId = mutableStateOf<Int?>(null)
    
    // Products
    val selectedProduct = mutableStateOf<Product?>(null)
    
    // Orders
    val selectedOrderId = mutableStateOf<Int?>(null)
    
    // To Buy
    val showToBuyScreen = mutableStateOf(false)
    val selectedToBuyItemIds = mutableStateOf<List<Int>>(emptyList())
    val showPurchaseHistory = mutableStateOf(false)
    
    // Order screens
    val showOrderList = mutableStateOf(false)
    val showOrderHistory = mutableStateOf(false)
    
    // Sales History
    val showSalesHistory = mutableStateOf(false)
    
    // Dialogs
    val showAddPersonDialog = mutableStateOf(false)
    val showAddProductDialog = mutableStateOf(false)
    val showAddDebtDialog = mutableStateOf(false)
    val showAddToBuyItemDialog = mutableStateOf(false)
    val showPaymentDialog = mutableStateOf(false)
    val showMarkAsBoughtDialog = mutableStateOf(false)
    val showNewOrderDialog = mutableStateOf(false)
    val showFilterDropdown = mutableStateOf(false)
    val showSettings = mutableStateOf(false)
    
    // ⭐ REMOVED: Success dialogs - no longer needed
    
    // Sidebar/Drawer
    val showDashboard = mutableStateOf(false)
    val showReports = mutableStateOf(false)
}

@Composable
fun rememberNavigationState(): NavigationState {
    return remember { NavigationState() }
}
