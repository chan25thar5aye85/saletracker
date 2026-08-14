package com.hninakari.saletracker.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.R
import com.hninakari.saletracker.data.model.*
import com.hninakari.saletracker.ui.screen.*
import com.hninakari.saletracker.viewmodel.*

@Composable
fun NavigationDestinations(
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
            OrderHistoryScreen(
                viewModel = orderViewModel,
                onBack = { navState.showOrderHistory.value = false }
            )
        }
        navState.showOrderList.value -> {
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
        navState.showPurchaseHistory.value -> {
            PurchaseHistoryScreen(
                viewModel = toBuyViewModel,
                onBack = { navState.showPurchaseHistory.value = false }
            )
        }
        navState.showToBuyScreen.value -> {
            ToBuyScreen(
                viewModel = toBuyViewModel,
                onAddItem = { navState.showAddToBuyItemDialog.value = true },
                onMarkBought = { itemIds ->
                    navState.selectedToBuyItemIds.value = itemIds
                    navState.showMarkAsBoughtDialog.value = true
                },
                onCreateOrder = { itemIds ->
                    navState.showNewOrderDialog.value = true
                    navState.selectedToBuyItemIds.value = itemIds
                },
                onHistoryClick = { navState.showPurchaseHistory.value = true }
            )
        }
        navState.currentScreen.value == "payment_history" -> {
            PaymentHistoryScreen(
                debtId = navState.selectedDebtId.value!!,
                debtViewModel = debtViewModel,
                onBack = {
                    navState.currentScreen.value = "person_detail"
                    navState.selectedDebtId.value = null
                }
            )
        }
        navState.currentScreen.value == "person_detail" -> {
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
        navState.currentScreen.value == "debt_list" -> {
            DebtListScreen(
                personViewModel = personViewModel,
                debtViewModel = debtViewModel,
                onPersonClick = { person ->
                    navState.selectedPerson.value = person
                    navState.currentScreen.value = "person_detail"
                }
            )
        }
        else -> {
            when (navState.selectedTab.value) {
                0 -> {
                    SaleEntryScreen(
                        onSaleAdded = onAddSaleSuccess
                    )
                }
                1 -> {
                    ExpenseEntryScreen(
                        onExpenseAdded = onAddExpenseSuccess
                    )
                }
                2 -> {
                    TransferEntryScreen(
                        onTransferAdded = onAddTransferSuccess
                    )
                }
                3 -> {
                    PersonListScreen(
                        personViewModel = personViewModel,
                        onPersonClick = { person ->
                            navState.selectedPerson.value = person
                            navState.currentScreen.value = "person_detail"
                        },
                        onAddClick = onShowAddPersonDialog
                    )
                }
                4 -> {
                    // To Buy Screen
                    ToBuyScreen(
                        viewModel = toBuyViewModel,
                        onAddItem = { navState.showAddToBuyItemDialog.value = true },
                        onMarkBought = { itemIds ->
                            navState.selectedToBuyItemIds.value = itemIds
                            navState.showMarkAsBoughtDialog.value = true
                        },
                        onCreateOrder = { itemIds ->
                            navState.showNewOrderDialog.value = true
                            navState.selectedToBuyItemIds.value = itemIds
                        },
                        onHistoryClick = { navState.showPurchaseHistory.value = true }
                    )
                }
            }
        }
    }
}
