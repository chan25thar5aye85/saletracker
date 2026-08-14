package com.hninakari.saletracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductSupplierDao {
    @Query("SELECT * FROM product_suppliers WHERE isDeleted = 0 AND productId = :productId")
    fun getSuppliersForProduct(productId: Int): Flow<List<ProductSupplierEntity>>
    
    @Query("SELECT * FROM product_suppliers WHERE isDeleted = 0 AND supplierPersonId = :supplierId")
    fun getProductsForSupplier(supplierId: Int): Flow<List<ProductSupplierEntity>>
    
    @Query("SELECT * FROM product_suppliers WHERE isDeleted = 0 AND productId = :productId AND isDefault = 1 LIMIT 1")
    suspend fun getDefaultSupplierForProduct(productId: Int): ProductSupplierEntity?
    
    @Query("SELECT * FROM product_suppliers WHERE isDeleted = 0 AND productId = :productId AND supplierPersonId = :supplierId LIMIT 1")
    suspend fun getSupplierPrice(productId: Int, supplierId: Int): ProductSupplierEntity?
    
    @Insert
    suspend fun insertProductSupplier(supplier: ProductSupplierEntity): Long
    
    @Update
    suspend fun updateProductSupplier(supplier: ProductSupplierEntity)
    
    @Query("UPDATE product_suppliers SET isDefault = 0 WHERE productId = :productId AND isDefault = 1")
    suspend fun clearDefaultSupplier(productId: Int)
    
    @Query("UPDATE product_suppliers SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteProductSupplier(id: Int)
}
