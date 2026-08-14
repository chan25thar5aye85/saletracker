package com.hninakari.saletracker.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    currentUserId: String = "default-user",
    onSaveUserId: (String) -> Unit = {},
    onAddSaleSuccess: (Sale) -> Unit,
    onAddExpenseSuccess: (Expense) -> Unit,
    onAddTransferSuccess: (Transfer) -> Unit,
    onShowAddPersonDialog: () -> Unit,
    onShowAddProductDialog: () -> Unit
) {
    when {
        navState.showOrderHistory.value -> {
            Box(modifier = Modifier.fillMaxSize()) {
                OrderHistoryScreen(
                    viewModel = orderViewModel,
                    onBack = { navState.showOrderHistory.value = false }
                )
            }
        }
        navState.showOrderList.value -> {
            Box(modifier = Modifier.fillMaxSize()) {
                OrderListScreen(
                    viewModel = orderViewModel,
                    onOrderClick = { orderId ->
                        navState.selectedOrderId.value = orderId
                    },
                    onNewOrder = { navState.showNewOrderDialog.value = true },
                    onCancelOrder = { orderId ->
                        orderViewModel.cancelOrder(orderId)
                    },
                    onCompleteOrder = { orderId ->
                        orderViewModel.completeOrder(orderId, null)
                    }
                )
            }
        }
        navState.showPurchaseHistory.value -> {
            Box(modifier = Modifier.fillMaxSize()) {
                PurchaseHistoryScreen(
                    viewModel = toBuyViewModel,
                    onBack = { navState.showPurchaseHistory.value = false }
                )
            }
        }
        navState.currentScreen.value == "payment_history" -> {
            Box(modifier = Modifier.fillMaxSize()) {
                PaymentHistoryScreen(
                    debtId = navState.selectedDebtId.value!!,
                    debtViewModel = debtViewModel,
                    onBack = {
                        navState.currentScreen.value = "person_detail"
                        navState.selectedDebtId.value = null
                    }
                )
            }
        }
        navState.currentScreen.value == "person_detail" -> {
            Box(modifier = Modifier.fillMaxSize()) {
                PersonDetailScreen(
                    person = navState.selectedPerson.value!!,
                    debtViewModel = debtViewModel,
                    onBack = {
                        navState.currentScreen.value = "main"
                        navState.selectedPerson.value = null
                        navState.selectedTab.value = 3
                    },
                    onAddDebt = { navState.showAddDebtDialog.value = true },
                    onPayDebt = { debt ->
                        navState.selectedDebtId.value = debt.id
                        navState.showPaymentDialog.value = true
                    },
                    onPayAllDebt = { debt ->
                        debtViewModel.payAllDebt(debt.id)
                    },
                    onViewHistory = { debtId ->
                        navState.selectedDebtId.value = debtId
                        navState.currentScreen.value = "payment_history"
                    }
                )
            }
        }
        navState.currentScreen.value == "debt_list" -> {
            Box(modifier = Modifier.fillMaxSize()) {
                DebtListScreen(
                    personViewModel = personViewModel,
                    debtViewModel = debtViewModel,
                    onPersonClick = { person ->
                        navState.selectedPerson.value = person
                        navState.currentScreen.value = "person_detail"
                    }
                )
            }
        }
    }
}
