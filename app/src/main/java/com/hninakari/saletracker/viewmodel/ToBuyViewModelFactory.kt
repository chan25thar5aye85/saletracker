package com.hninakari.saletracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hninakari.saletracker.data.repository.ProductRepository
import com.hninakari.saletracker.data.repository.ToBuyRepository

class ToBuyViewModelFactory(
    private val toBuyRepository: ToBuyRepository,
    private val productRepository: ProductRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToBuyViewModel::class.java)) {
            return ToBuyViewModel(toBuyRepository, productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
