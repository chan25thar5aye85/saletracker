package com.hninakari.saletracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ToBuyItemDao {
    @Query("SELECT * FROM to_buy_items WHERE isDeleted = 0 AND isBought = 0 AND orderId IS NULL ORDER BY priority DESC, dateAdded DESC")
    fun getActiveToBuyItems(): Flow<List<ToBuyItemEntity>>

    @Query("SELECT * FROM to_buy_items WHERE isDeleted = 0 AND isBought = 0 AND orderId = :orderId ORDER BY priority DESC, dateAdded DESC")
    fun getToBuyItemsByOrder(orderId: Int): Flow<List<ToBuyItemEntity>>

    @Query("SELECT * FROM to_buy_items WHERE isDeleted = 0 AND isBought = 1 ORDER BY dateAdded DESC")
    fun getBoughtToBuyItems(): Flow<List<ToBuyItemEntity>>

    @Query("SELECT * FROM to_buy_items WHERE isDeleted = 0 AND productId = :productId AND isBought = 0 AND orderId IS NULL")
    fun getActiveByProduct(productId: Int): Flow<List<ToBuyItemEntity>>

    @Query("SELECT * FROM to_buy_items ORDER BY dateAdded DESC")
    fun getAllToBuyItems(): Flow<List<ToBuyItemEntity>>

    @Insert
    suspend fun insertToBuyItem(item: ToBuyItemEntity): Long

    @Update
    suspend fun updateToBuyItem(item: ToBuyItemEntity)

    @Query("UPDATE to_buy_items SET isBought = 1 WHERE id = :itemId")
    suspend fun markAsBought(itemId: Int)

    @Query("UPDATE to_buy_items SET orderId = :orderId WHERE id = :itemId")
    suspend fun assignToOrder(itemId: Int, orderId: Int)

    @Query("UPDATE to_buy_items SET orderId = NULL WHERE id = :itemId")
    suspend fun removeFromOrder(itemId: Int)

    @Query("UPDATE to_buy_items SET isDeleted = 1 WHERE id = :itemId")
    suspend fun softDeleteToBuyItem(itemId: Int)

    @Query("SELECT COUNT(*) FROM to_buy_items WHERE isDeleted = 0 AND isBought = 0 AND orderId IS NULL")
    suspend fun getActiveCount(): Int

    @Query("SELECT SUM(quantity) FROM to_buy_items WHERE isDeleted = 0 AND isBought = 0 AND orderId IS NULL")
    suspend fun getTotalQuantity(): Int?
}
