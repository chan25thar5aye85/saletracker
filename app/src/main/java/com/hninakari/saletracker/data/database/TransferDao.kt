package com.hninakari.saletracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferDao {
    @Query("SELECT * FROM transfers WHERE isDeleted = 0 ORDER BY date DESC")
    fun getActiveTransfers(): Flow<List<TransferEntity>>
    
    @Query("SELECT * FROM transfers WHERE isDeleted = 0 AND date >= :startDate ORDER BY date DESC")
    fun getTransfersByDateRange(startDate: Long): Flow<List<TransferEntity>>
    
    @Query("SELECT * FROM transfers ORDER BY date DESC")
    fun getAllTransfers(): Flow<List<TransferEntity>>
    
    @Insert
    suspend fun insertTransfer(transfer: TransferEntity)
    
    @Update
    suspend fun updateTransfer(transfer: TransferEntity)
    
    @Query("UPDATE transfers SET isDeleted = 1 WHERE id = :transferId")
    suspend fun softDeleteTransfer(transferId: Int)
    
    @Query("SELECT SUM(fee) FROM transfers WHERE isDeleted = 0 AND date >= :startDate")
    suspend fun getTotalFeesByDateRange(startDate: Long): Double?
    
    @Query("SELECT COUNT(*) FROM transfers WHERE isDeleted = 0")
    suspend fun getActiveCount(): Int
}
