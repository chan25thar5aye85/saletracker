package com.hninakari.saletracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hninakari.saletracker.data.model.Product
import com.hninakari.saletracker.data.model.ProductSupplier
import com.hninakari.saletracker.data.model.ProductWithSuppliers
import com.hninakari.saletracker.data.repository.ProductRepository
import com.hninakari.saletracker.data.repository.ProductSupplierRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val productSupplierRepository: ProductSupplierRepository
) : ViewModel() {
    
    val allProducts: Flow<List<Product>> = productRepository.getAllProducts()
    
    fun searchProducts(query: String): Flow<List<Product>> {
        return productRepository.searchProducts(query)
    }
    
    fun addProduct(name: String, price: Double) {
        viewModelScope.launch {
            val product = Product(
                name = name,
                price = price
            )
            productRepository.addProduct(product)
        }
    }
    
    fun updateProduct(product: Product) {
        viewModelScope.launch {
            productRepository.updateProduct(product)
        }
    }
    
    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
        }
    }
    
    // Product Supplier methods
    fun getSuppliersForProduct(productId: Int): Flow<List<ProductSupplier>> {
        return productSupplierRepository.getSuppliersForProduct(productId)
    }
    
    suspend fun getDefaultSupplier(productId: Int): ProductSupplier? {
        return productSupplierRepository.getDefaultSupplier(productId)
    }
    
    suspend fun addSupplierPrice(supplier: ProductSupplier) {
        productSupplierRepository.addSupplierPrice(supplier)
    }
    
    suspend fun updateSupplierPrice(supplier: ProductSupplier) {
        productSupplierRepository.updateSupplierPrice(supplier)
    }
    
    suspend fun setDefaultSupplier(productId: Int, supplierId: Int) {
        productSupplierRepository.setDefaultSupplier(productId, supplierId)
    }
    
    suspend fun deleteSupplierPrice(id: Int) {
        productSupplierRepository.deleteSupplierPrice(id)
    }
}
