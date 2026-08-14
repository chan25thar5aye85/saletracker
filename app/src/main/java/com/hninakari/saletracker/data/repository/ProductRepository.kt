package com.hninakari.saletracker.data.repository

import android.content.Context
import com.hninakari.saletracker.core.SyncTrigger
import com.hninakari.saletracker.data.database.PurchaseDatabase
import com.hninakari.saletracker.data.database.ProductEntity
import com.hninakari.saletracker.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepository(context: Context) {

    private val database = PurchaseDatabase.getDatabase(context)
    private val productDao = database.productDao()

    private fun toEntity(product: Product): ProductEntity {
        return ProductEntity(
            id = product.id,
            name = product.name,
            price = product.price,
            isDeleted = product.isDeleted
        )
    }

    private fun toModel(entity: ProductEntity): Product {
        return Product(
            id = entity.id,
            name = entity.name,
            price = entity.price,
            isDeleted = entity.isDeleted
        )
    }

    fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { entities ->
            entities.map { toModel(it) }
        }
    }

    suspend fun getProductById(productId: Int): Product? {
        val entity = productDao.getProductById(productId)
        return entity?.let { toModel(it) }
    }

    fun searchProducts(query: String): Flow<List<Product>> {
        return productDao.searchProducts(query).map { entities ->
            entities.map { toModel(it) }
        }
    }

    suspend fun addProduct(product: Product) {
        productDao.insertProduct(toEntity(product))
        SyncTrigger.triggerUpload()
    }

    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(toEntity(product))
        SyncTrigger.triggerUpload()
    }

    suspend fun deleteProduct(productId: Int) {
        productDao.softDeleteProduct(productId)
        SyncTrigger.triggerUpload()
    }
}
