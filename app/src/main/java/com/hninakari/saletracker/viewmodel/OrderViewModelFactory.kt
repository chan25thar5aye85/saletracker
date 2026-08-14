package com.hninakari.saletracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hninakari.saletracker.data.repository.OrderRepository
import com.hninakari.saletracker.data.repository.ProductRepository
import com.hninakari.saletracker.data.repository.PersonRepository

class OrderViewModelFactory(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val personRepository: PersonRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return OrderViewModel(orderRepository, productRepository, personRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
