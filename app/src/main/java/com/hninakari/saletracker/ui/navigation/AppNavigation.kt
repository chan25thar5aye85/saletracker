package com.hninakari.saletracker.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hninakari.saletracker.R
import com.hninakari.saletracker.SaleTrackerApplication
import com.hninakari.saletracker.core.UserPreferences
import com.hninakari.saletracker.data.model.*
import com.hninakari.saletracker.data.repository.*
import com.hninakari.saletracker.ui.components.AppBottomBar
import com.hninakari.saletracker.ui.components.AppTopBar
import com.hninakari.saletracker.ui.components.SuccessDialogs
import com.hninakari.saletracker.ui.screen.*
import com.hninakari.saletracker.viewmodel.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    saleRepository: SaleRepository,
    expenseRepository: ExpenseRepository,
    transferRepository: TransferRepository,
    personRepository: PersonRepository,
    debtRepository: DebtRepository,
    productRepository: ProductRepository,
    toBuyRepository: ToBuyRepository,
    productSupplierRepository: ProductSupplierRepository,
    orderRepository: OrderRepository
) {
    val navState = rememberNavigationState()
    val context = LocalContext.current
    val application = context.applicationContext as? SaleTrackerApplication
    
    val userPrefs = remember { UserPreferences.getInstance(context) }
    val currentUserId by userPrefs.userId.collectAsState()
    
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty() && currentUserId != "default-user") {
            application?.startRealtimeListening()
        }
    }
    
    val saleViewModel: SaleViewModel = viewModel(factory = SaleViewModelFactory(saleRepository))
    val expenseViewModel: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory(expenseRepository))
    val transferViewModel: TransferViewModel = viewModel(factory = TransferViewModelFactory(transferRepository))
    val personViewModel: PersonViewModel = viewModel(factory = PersonViewModelFactory(personRepository))
    val debtViewModel: DebtViewModel = viewModel(factory = DebtViewModelFactory(debtRepository))
    val productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(productRepository, productSupplierRepository)
    )
    val toBuyViewModel: ToBuyViewModel = viewModel(
        factory = ToBuyViewModelFactory(toBuyRepository, productRepository)
    )
    val orderViewModel: OrderViewModel = viewModel(
        factory = OrderViewModelFactory(orderRepository, productRepository, personRepository)
    )
    val profitViewModel: ProfitViewModel = viewModel(
        factory = ProfitViewModelFactory(saleRepository, expenseRepository, transferRepository)
    )
    
    if (navState.showSettings.value) {
        UserSettingsScreen(
            currentUserId = currentUserId,
            onSaveUserId = { userId ->
                userPrefs.saveUserId(userId)
                application?.stopRealtimeListening()
                application?.startRealtimeListening()
            },
            onBack = {
                navState.showSettings.value = false
            }
        )
        return
    }
    
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val currentDate = dateFormat.format(Date())
    
    val isDetailScreen = navState.currentScreen.value != "main"
    val isToBuyOrHistory = navState.showToBuyScreen.value || navState.showPurchaseHistory.value
    val isOrderScreen = navState.showOrderList.value || navState.showOrderHistory.value
    
    // ⭐ Pager State for Circular Swipe
    // Using a large virtual count to simulate infinite scrolling
    val virtualPageCount = 1000 // Large enough to feel infinite
    val pagerState = rememberPagerState(
        initialPage = virtualPageCount / 2, // Start in the middle
        pageCount = { virtualPageCount }
    )
    
    // ⭐ Map virtual page to actual tab (0-4)
    fun getActualPage(virtualPage: Int): Int {
        return virtualPage % 5
    }
    
    // ⭐ Sync bottom bar with pager (using actual page)
    LaunchedEffect(pagerState.currentPage) {
        val actualPage = getActualPage(pagerState.currentPage)
        if (navState.selectedTab.value != actualPage) {
            navState.selectedTab.value = actualPage
        }
    }
    
    // ⭐ Sync pager with bottom bar selection
    LaunchedEffect(navState.selectedTab.value) {
        val currentActual = getActualPage(pagerState.currentPage)
        if (currentActual != navState.selectedTab.value) {
            // Calculate target page that matches the desired actual page
            val currentVirtual = pagerState.currentPage
            val targetVirtual = currentVirtual + (navState.selectedTab.value - currentActual)
            pagerState.animateScrollToPage(targetVirtual)
        }
    }
    
    val actualPage = getActualPage(pagerState.currentPage)
    
    val screenTitle = when {
        navState.currentScreen.value == "person_detail" && navState.selectedPerson.value != null -> 
            navState.selectedPerson.value?.name ?: ""
        navState.currentScreen.value == "debt_list" -> "အကြွေးစာရင်း"
        navState.currentScreen.value == "payment_history" -> "ငွေပေးချေမှုမှတ်တမ်း"
        navState.showToBuyScreen.value -> "ဈေးဝယ်စာရင်း"
        navState.showPurchaseHistory.value -> "ဝယ်ယူမှုမှတ်တမ်း"
        navState.showOrderList.value -> "အမှာစာများ"
        navState.showOrderHistory.value -> "အမှာစာမှတ်တမ်း"
        actualPage == 0 -> stringResource(R.string.add_sale)
        actualPage == 1 -> stringResource(R.string.add_expense)
        actualPage == 2 -> stringResource(R.string.add_transfer)
        actualPage == 3 -> stringResource(R.string.people)
        actualPage == 4 -> "To Buy"
        else -> "Akari"
    }
    
    val screenSubtitle = when {
        actualPage == 0 -> currentDate
        actualPage == 1 -> currentDate
        actualPage == 2 -> currentDate
        else -> ""
    }
    
    val showBackButton = isDetailScreen || isToBuyOrHistory || isOrderScreen
    
    val onAddSaleSuccess: (Sale) -> Unit = { sale ->
        saleViewModel.addSale(sale)
        navState.lastSale.value = sale
        navState.showSaleSuccess.value = true
    }
    
    val onAddExpenseSuccess: (Expense) -> Unit = { expense ->
        expenseViewModel.addExpense(expense)
        navState.lastExpense.value = expense
        navState.showExpenseSuccess.value = true
    }
    
    val onAddTransferSuccess: (Transfer) -> Unit = { transfer ->
        transferViewModel.addTransfer(transfer)
        navState.lastTransfer.value = transfer
        navState.showTransferSuccess.value = true
    }
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = screenTitle,
                subtitle = screenSubtitle,
                showBack = showBackButton,
                onBack = {
                    when {
                        navState.showOrderHistory.value -> navState.showOrderHistory.value = false
                        navState.showOrderList.value -> navState.showOrderList.value = false
                        navState.showToBuyScreen.value -> {
                            navState.showToBuyScreen.value = false
                            navState.selectedToBuyItemIds.value = emptyList()
                        }
                        navState.showPurchaseHistory.value -> navState.showPurchaseHistory.value = false
                        navState.currentScreen.value == "person_detail" -> {
                            navState.currentScreen.value = "main"
                            navState.selectedPerson.value = null
                            navState.selectedTab.value = 3
                        }
                        navState.currentScreen.value == "debt_list" -> {
                            navState.currentScreen.value = "main"
                            navState.selectedTab.value = 3
                        }
                        navState.currentScreen.value == "payment_history" -> {
                            navState.currentScreen.value = "person_detail"
                            navState.selectedDebtId.value = null
                        }
                    }
                },
                showFilter = false,
                onFilterClick = { },
                filterExpanded = false,
                onFilterSelected = { },
                currentFilter = saleViewModel.selectedFilter.value,
                showLanguage = (actualPage == 0 || actualPage == 3 || actualPage == 4) && 
                    !isDetailScreen && !isToBuyOrHistory && !isOrderScreen,
                onLanguageClick = { navState.showLanguageDialog.value = true },
                showAddPerson = actualPage == 3 && !isDetailScreen,
                onAddPersonClick = { navState.showAddPersonDialog.value = true },
                showAddDebt = navState.currentScreen.value == "person_detail" && navState.selectedPerson.value != null,
                onAddDebtClick = { navState.showAddDebtDialog.value = true },
                showSettings = (actualPage == 3 || actualPage == 4) && 
                    !isDetailScreen && !isToBuyOrHistory && !isOrderScreen,
                onSettingsClick = { navState.showSettings.value = true }
            )
        },
        bottomBar = {
            if (!isDetailScreen && !isToBuyOrHistory && !isOrderScreen) {
                AppBottomBar(
                    selectedTab = actualPage,
                    onTabSelected = { tab ->
                        if (actualPage != tab) {
                            navState.selectedTab.value = tab
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ⭐ Circular Swipe Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { virtualPage ->
                val page = virtualPage % 5
                when (page) {
                    0 -> SaleEntryScreen(onSaleAdded = onAddSaleSuccess)
                    1 -> ExpenseEntryScreen(onExpenseAdded = onAddExpenseSuccess)
                    2 -> TransferEntryScreen(onTransferAdded = onAddTransferSuccess)
                    3 -> PersonListScreen(
                        personViewModel = personViewModel,
                        onPersonClick = { person ->
                            navState.selectedPerson.value = person
                            navState.currentScreen.value = "person_detail"
                        },
                        onAddClick = { navState.showAddPersonDialog.value = true }
                    )
                    4 -> ToBuyScreen(
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
            
            NavigationDestinationsOverlay(
                navState = navState,
                saleViewModel = saleViewModel,
                expenseViewModel = expenseViewModel,
                transferViewModel = transferViewModel,
                personViewModel = personViewModel,
                debtViewModel = debtViewModel,
                productViewModel = productViewModel,
                toBuyViewModel = toBuyViewModel,
                orderViewModel = orderViewModel,
                profitViewModel = profitViewModel,
                currentUserId = currentUserId,
                onSaveUserId = { userId ->
                    userPrefs.saveUserId(userId)
                },
                onAddSaleSuccess = onAddSaleSuccess,
                onAddExpenseSuccess = onAddExpenseSuccess,
                onAddTransferSuccess = onAddTransferSuccess,
                onShowAddPersonDialog = { navState.showAddPersonDialog.value = true },
                onShowAddProductDialog = { navState.showAddProductDialog.value = true }
            )
        }
    }
    
    DialogManager(
        navState = navState,
        personViewModel = personViewModel,
        debtViewModel = debtViewModel,
        productViewModel = productViewModel,
        toBuyViewModel = toBuyViewModel,
        orderViewModel = orderViewModel
    )
    
    SuccessDialogs(
        showSaleSuccess = navState.showSaleSuccess.value,
        showExpenseSuccess = navState.showExpenseSuccess.value,
        showTransferSuccess = navState.showTransferSuccess.value,
        lastSale = navState.lastSale.value,
        lastExpense = navState.lastExpense.value,
        lastTransfer = navState.lastTransfer.value,
        onSaleDismiss = { navState.showSaleSuccess.value = false },
        onExpenseDismiss = { navState.showExpenseSuccess.value = false },
        onTransferDismiss = { navState.showTransferSuccess.value = false }
    )
}
