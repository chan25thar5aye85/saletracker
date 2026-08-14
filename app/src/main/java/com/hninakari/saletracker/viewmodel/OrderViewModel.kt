package com.hninakari.saletracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hninakari.saletracker.data.model.Order
import com.hninakari.saletracker.data.model.OrderItem
import com.hninakari.saletracker.data.repository.OrderRepository
import com.hninakari.saletracker.data.repository.ProductRepository
import com.hninakari.saletracker.data.repository.PersonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val personRepository: PersonRepository
) : ViewModel() {

    val draftOrders: Flow<List<Order>> = orderRepository.getDraftOrders()

    val purchasedOrders: Flow<List<Order>> = orderRepository.getPurchasedOrders()

    suspend fun getOrderById(orderId: Int): Order? {
        return orderRepository.getOrderById(orderId)
    }

    fun getItemsForOrder(orderId: Int): Flow<List<OrderItem>> {
        return orderRepository.getItemsForOrder(orderId)
    }

    fun createOrder(
        supplierPersonId: Int?,
        note: String,
        toBuyItemIds: List<Int>,
        onResult: (Order?) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val order = orderRepository.createOrder(supplierPersonId, note, toBuyItemIds)
                onResult(order)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    fun completeOrder(orderId: Int, expenseId: Int?) {
        viewModelScope.launch {
            orderRepository.completeOrder(orderId, expenseId)
        }
    }

    fun cancelOrder(orderId: Int) {
        viewModelScope.launch {
            orderRepository.cancelOrder(orderId)
        }
    }

    fun deleteOrder(orderId: Int) {
        viewModelScope.launch {
            orderRepository.deleteOrder(orderId)
        }
    }
}
