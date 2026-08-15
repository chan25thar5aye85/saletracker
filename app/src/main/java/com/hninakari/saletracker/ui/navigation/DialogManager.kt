package com.hninakari.saletracker.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import com.hninakari.saletracker.data.model.*
import com.hninakari.saletracker.ui.screen.*
import com.hninakari.saletracker.viewmodel.*

@Composable
fun DialogManager(
    navState: NavigationState,
    personViewModel: PersonViewModel,
    debtViewModel: DebtViewModel,
    productViewModel: ProductViewModel,
    toBuyViewModel: ToBuyViewModel,
    orderViewModel: OrderViewModel
) {
    val context = LocalContext.current
    val allDebts by debtViewModel.allDebts.collectAsState(initial = emptyList())
    
    // REMOVED: Language Dialog - now in Settings screen
    
    // Add Person Dialog
    if (navState.showAddPersonDialog.value) {
        AddPersonDialog(
            onDismiss = { navState.showAddPersonDialog.value = false },
            onAddPerson = { name, phone, type, notes ->
                personViewModel.addPerson(name, phone, type, notes)
                navState.showAddPersonDialog.value = false
            }
        )
    }
    
    // Add Debt Dialog
    if (navState.showAddDebtDialog.value && navState.selectedPerson.value != null) {
        DebtEntryDialog(
            personName = navState.selectedPerson.value!!.name,
            onDismiss = { navState.showAddDebtDialog.value = false },
            onAddDebt = { type, amount, note ->
                debtViewModel.addDebt(navState.selectedPerson.value!!.id, type, amount, note)
                navState.showAddDebtDialog.value = false
            }
        )
    }
    
    // Payment Dialog
    if (navState.showPaymentDialog.value && navState.selectedDebtId.value != null) {
        val selectedDebt = allDebts.find { it.id == navState.selectedDebtId.value }
        if (selectedDebt != null) {
            PaymentDialog(
                debt = selectedDebt,
                onDismiss = {
                    navState.showPaymentDialog.value = false
                    navState.selectedDebtId.value = null
                },
                onPayment = { amount, note ->
                    debtViewModel.makePayment(navState.selectedDebtId.value!!, amount, note)
                    navState.showPaymentDialog.value = false
                    navState.selectedDebtId.value = null
                }
            )
        }
    }
    
    // Add To Buy Item Dialog
    if (navState.showAddToBuyItemDialog.value) {
        val products by productViewModel.allProducts.collectAsState(initial = emptyList())
        AddToBuyItemDialog(
            products = products,
            onDismiss = { navState.showAddToBuyItemDialog.value = false },
            onAdd = { productId, quantity, priority, note ->
                toBuyViewModel.addToBuyItem(productId, quantity, priority, note)
                navState.showAddToBuyItemDialog.value = false
            }
        )
    }
    
    // Mark As Bought Dialog
    if (navState.showMarkAsBoughtDialog.value && navState.selectedToBuyItemIds.value.isNotEmpty()) {
        val items by toBuyViewModel.activeItemsWithDetails.collectAsState(initial = emptyList())
        val selectedItems = items.filter { it.item.id in navState.selectedToBuyItemIds.value }
        val suppliers by personViewModel.allPeople.collectAsState(initial = emptyList())
        val supplierList = suppliers.filter { it.type == PersonType.SUPPLIER || it.type == PersonType.OTHER }
        
        MarkAsBoughtDialog(
            items = selectedItems,
            suppliers = supplierList,
            onDismiss = {
                navState.showMarkAsBoughtDialog.value = false
                navState.selectedToBuyItemIds.value = emptyList()
            },
            onConfirm = { supplierId, note, _ ->
                navState.showMarkAsBoughtDialog.value = false
                navState.selectedToBuyItemIds.value = emptyList()
            }
        )
    }
    
    // New Order Dialog
    if (navState.showNewOrderDialog.value) {
        val items by toBuyViewModel.activeItemsWithDetails.collectAsState(initial = emptyList())
        val selectedItems = items.filter { it.item.id in navState.selectedToBuyItemIds.value }
        val suppliers by personViewModel.allPeople.collectAsState(initial = emptyList())
        val supplierList = suppliers.filter { it.type == PersonType.SUPPLIER || it.type == PersonType.OTHER }
        
        NewOrderDialog(
            items = selectedItems,
            suppliers = supplierList,
            onDismiss = {
                navState.showNewOrderDialog.value = false
                navState.selectedToBuyItemIds.value = emptyList()
            },
            onCreate = { supplierId, note, itemIds ->
                orderViewModel.createOrder(supplierId, note, itemIds) { order ->
                    if (order != null) {
                        navState.showNewOrderDialog.value = false
                        navState.selectedToBuyItemIds.value = emptyList()
                        navState.showOrderList.value = true
                    }
                }
            }
        )
    }
    
    // Add Product Dialog
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
