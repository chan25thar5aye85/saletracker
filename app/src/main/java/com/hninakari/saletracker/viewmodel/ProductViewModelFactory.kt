package com.hninakari.saletracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hninakari.saletracker.data.repository.ProductRepository
import com.hninakari.saletracker.data.repository.ProductSupplierRepository

class ProductViewModelFactory(
    private val productRepository: ProductRepository,
    private val productSupplierRepository: ProductSupplierRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(productRepository, productSupplierRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
