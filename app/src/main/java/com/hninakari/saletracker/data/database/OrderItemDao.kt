package com.hninakari.saletracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDao {
    @Query("SELECT * FROM order_items WHERE isDeleted = 0 AND orderId = :orderId")
    fun getItemsForOrder(orderId: Int): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE isDeleted = 0 ORDER BY orderId DESC")
    fun getAllOrderItems(): Flow<List<OrderItemEntity>>

    @Insert
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("UPDATE order_items SET isDeleted = 1 WHERE orderId = :orderId")
    suspend fun deleteItemsByOrder(orderId: Int)
}
