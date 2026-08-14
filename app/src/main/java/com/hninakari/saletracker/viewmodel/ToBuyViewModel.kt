package com.hninakari.saletracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hninakari.saletracker.data.model.PurchaseBatchItem
import com.hninakari.saletracker.data.model.ToBuyItem
import com.hninakari.saletracker.data.repository.ToBuyRepository
import com.hninakari.saletracker.data.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ToBuyViewModel(
    private val toBuyRepository: ToBuyRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    
    val activeItems: Flow<List<ToBuyItem>> = toBuyRepository.getActiveToBuyItems()
    
    val activeItemsWithDetails: Flow<List<ToBuyItemWithProduct>> = activeItems.combine(
        productRepository.getAllProducts()
    ) { items, products ->
        items.map { item ->
            val product = products.find { it.id == item.productId }
            ToBuyItemWithProduct(
                item = item,
                product = product
            )
        }.filter { it.product != null }
    }
    
    val allBatches = toBuyRepository.getAllBatches()
    
    suspend fun getItemsForBatch(batchId: Int): Flow<List<PurchaseBatchItem>> {
        return toBuyRepository.getItemsForBatch(batchId)
    }
    
    suspend fun getBatchById(batchId: Int): com.hninakari.saletracker.data.model.PurchaseBatch? {
        return toBuyRepository.getBatchById(batchId)
    }
    
    fun addToBuyItem(productId: Int, quantity: Int, priority: String, note: String) {
        viewModelScope.launch {
            val item = ToBuyItem(
                productId = productId,
                quantity = quantity,
                priority = com.hninakari.saletracker.data.model.Priority.valueOf(priority),
                note = note
            )
            toBuyRepository.addToBuyItem(item)
        }
    }
    
    fun updateToBuyItem(item: ToBuyItem) {
        viewModelScope.launch {
            toBuyRepository.updateToBuyItem(item)
        }
    }
    
    fun markAsBought(itemId: Int) {
        viewModelScope.launch {
            toBuyRepository.markAsBought(itemId)
        }
    }
    
    fun deleteToBuyItem(itemId: Int) {
        viewModelScope.launch {
            toBuyRepository.deleteToBuyItem(itemId)
        }
    }
    
    suspend fun createPurchaseBatch(
        supplierPersonId: Int?,
        note: String,
        itemIds: List<Int>
    ): Long {
        return toBuyRepository.createPurchaseBatch(supplierPersonId, note, itemIds)
    }
}

data class ToBuyItemWithProduct(
    val item: ToBuyItem,
    val product: com.hninakari.saletracker.data.model.Product?
)
