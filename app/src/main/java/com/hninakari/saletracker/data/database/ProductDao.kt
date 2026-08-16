package com.hninakari.saletracker.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE isDeleted = 0 AND name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchProducts(query: String): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE isDeleted = 0 AND id = :productId")
    suspend fun getProductById(productId: Int): ProductEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)
    
    @Update
    suspend fun updateProduct(product: ProductEntity)
    
    @Query("UPDATE products SET isDeleted = 1 WHERE id = :productId")
    suspend fun softDeleteProduct(productId: Int)
}
