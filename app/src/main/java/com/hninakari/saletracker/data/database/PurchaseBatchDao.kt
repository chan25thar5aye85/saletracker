package com.hninakari.saletracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseBatchDao {
    @Query("SELECT * FROM purchase_batches WHERE isDeleted = 0 ORDER BY date DESC")
    fun getAllBatches(): Flow<List<PurchaseBatchEntity>>

    @Query("SELECT * FROM purchase_batches WHERE isDeleted = 0 AND id = :batchId")
    suspend fun getBatchById(batchId: Int): PurchaseBatchEntity?

    @Insert
    suspend fun insertBatch(batch: PurchaseBatchEntity): Long

    @Update
    suspend fun updateBatch(batch: PurchaseBatchEntity)

    @Query("UPDATE purchase_batches SET isDeleted = 1 WHERE id = :batchId")
    suspend fun softDeleteBatch(batchId: Int)
}
