package com.hninakari.saletracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE isDeleted = 0 ORDER BY date DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>
    
    @Query("SELECT * FROM orders WHERE isDeleted = 0 AND status = 'draft' ORDER BY date DESC")
    fun getDraftOrders(): Flow<List<OrderEntity>>
    
    @Query("SELECT * FROM orders WHERE isDeleted = 0 AND status = 'purchased' ORDER BY date DESC")
    fun getPurchasedOrders(): Flow<List<OrderEntity>>
    
    @Query("SELECT * FROM orders WHERE isDeleted = 0 AND id = :orderId")
    suspend fun getOrderById(orderId: Int): OrderEntity?
    
    @Insert
    suspend fun insertOrder(order: OrderEntity): Long
    
    @Update
    suspend fun updateOrder(order: OrderEntity)
    
    @Query("UPDATE orders SET status = 'purchased' WHERE id = :orderId")
    suspend fun markOrderAsPurchased(orderId: Int)
    
    @Query("UPDATE orders SET isDeleted = 1 WHERE id = :orderId")
    suspend fun softDeleteOrder(orderId: Int)
}
