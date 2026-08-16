package com.hninakari.saletracker.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import com.hninakari.saletracker.data.model.*
import com.hninakari.saletracker.ui.screen.*
import com.hninakari.saletracker.viewmodel.*

@Composable
fun NavigationDestinationsOverlay(
    navState: NavigationState,
    saleViewModel: SaleViewModel,
    expenseViewModel: ExpenseViewModel,
    transferViewModel: TransferViewModel,
    personViewModel: PersonViewModel,
    debtViewModel: DebtViewModel,
    productViewModel: ProductViewModel,
    toBuyViewModel: ToBuyViewModel,
    orderViewModel: OrderViewModel,
    profitViewModel: ProfitViewModel,
    currentUserId: String,
    onSaveUserId: (String) -> Unit,
    onAddSaleSuccess: (Sale) -> Unit,
    onAddExpenseSuccess: (Expense) -> Unit,
    onAddTransferSuccess: (Transfer) -> Unit,
    onShowAddPersonDialog: () -> Unit,
    onShowAddProductDialog: () -> Unit
) {
    val context = LocalContext.current

    // ------------------------------------------------------------
    // PERSON DETAIL
    // ------------------------------------------------------------
    
    if (navState.currentScreen.value == "person_detail" && navState.selectedPerson.value != null) {
        val person = navState.selectedPerson.value!!
        
        PersonDetailScreen(
            person = person,
            debtViewModel = debtViewModel,
            personViewModel = personViewModel,
            onBack = {
                navState.currentScreen.value = "main"
                navState.selectedPerson.value = null
                navState.selectedTab.value = 3
            },
            onAddDebt = { personId ->
                navState.selectedPerson.value = person
                navState.showAddDebtDialog.value = true
            },
            onPayDebt = { debt ->
                navState.selectedDebtId.value = debt.id
                navState.showPaymentDialog.value = true
            },
            onPayAllDebt = { debt ->
                debtViewModel.payAllDebt(debt.id)
            },
            onViewHistory = { person ->
                // Navigate to person debt history
                navState.selectedPerson.value = person
                navState.currentScreen.value = "person_debt_history"
            },
            onEditPerson = { person ->
                // Show edit person dialog
                navState.selectedPerson.value = person
                navState.showAddPersonDialog.value = true
            }
        )
        return
    }

    // ------------------------------------------------------------
    // PERSON DEBT HISTORY
    // ------------------------------------------------------------
    
    if (navState.currentScreen.value == "person_debt_history" && navState.selectedPerson.value != null) {
        val person = navState.selectedPerson.value!!
        
        PersonDebtHistoryScreen(
            person = person,
            debtViewModel = debtViewModel,
            onBack = {
                navState.currentScreen.value = "person_detail"
            }
        )
        return
    }

    // ------------------------------------------------------------
    // DEBT LIST
    // ------------------------------------------------------------
    
    if (navState.currentScreen.value == "debt_list") {
        DebtListScreen(
            personViewModel = personViewModel,
            debtViewModel = debtViewModel,
            onPersonClick = { person ->
                navState.selectedPerson.value = person
                navState.currentScreen.value = "person_detail"
            }
        )
        return
    }

    // ------------------------------------------------------------
    // PAYMENT HISTORY
    // ------------------------------------------------------------
    
    if (navState.currentScreen.value == "payment_history" && navState.selectedDebtId.value != null) {
        PaymentHistoryScreen(
            debtId = navState.selectedDebtId.value!!,
            debtViewModel = debtViewModel,
            onBack = {
                navState.currentScreen.value = "person_detail"
                navState.selectedDebtId.value = null
            }
        )
        return
    }

    // ------------------------------------------------------------
    // ADD PERSON DIALOG
    // ------------------------------------------------------------
    
    if (navState.showAddPersonDialog.value) {
        AddPersonDialog(
            onDismiss = { navState.showAddPersonDialog.value = false },
            onAddPerson = { name, phone, type, notes ->
                personViewModel.addPerson(name, phone, type, notes)
                navState.showAddPersonDialog.value = false
            }
        )
        return
    }

    // ------------------------------------------------------------
    // ADD PRODUCT DIALOG
    // ------------------------------------------------------------
    
    if (navState.showAddProductDialog.value) {
        AddProductDialog(
            product = navState.selectedProduct.value,
            onDismiss = {
                navState.showAddProductDialog.value = false
                navState.selectedProduct.value = null
            },
            onSave = { name, price ->
                val currentProduct = navState.selectedProduct.value
                if (currentProduct != null) {
                    val updated = currentProduct.copy(name = name, price = price)
                    productViewModel.updateProduct(updated)
                } else {
                    productViewModel.addProduct(name, price)
                }
                navState.showAddProductDialog.value = false
                navState.selectedProduct.value = null
            }
        )
    }
}
