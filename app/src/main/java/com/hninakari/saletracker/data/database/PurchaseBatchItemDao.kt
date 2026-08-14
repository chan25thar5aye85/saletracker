package com.hninakari.saletracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseBatchItemDao {
    @Query("SELECT * FROM purchase_batch_items WHERE isDeleted = 0 AND batchId = :batchId")
    fun getItemsForBatch(batchId: Int): Flow<List<PurchaseBatchItemEntity>>

    @Insert
    suspend fun insertBatchItem(item: PurchaseBatchItemEntity): Long

    @Insert
    suspend fun insertBatchItems(items: List<PurchaseBatchItemEntity>)

    @Query("UPDATE purchase_batch_items SET isDeleted = 1 WHERE batchId = :batchId")
    suspend fun softDeleteByBatch(batchId: Int)
}
