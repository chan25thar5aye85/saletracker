package com.hninakari.saletracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales WHERE isDeleted = 0 ORDER BY date DESC")
    fun getActiveSales(): Flow<List<SaleEntity>>
    
    @Query("SELECT * FROM sales WHERE isDeleted = 0 AND date >= :startDate ORDER BY date DESC")
    fun getSalesByDateRange(startDate: Long): Flow<List<SaleEntity>>
    
    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSales(): Flow<List<SaleEntity>>
    
    @Insert
    suspend fun insertSale(sale: SaleEntity)
    
    @Update
    suspend fun updateSale(sale: SaleEntity)
    
    @Query("UPDATE sales SET isDeleted = 1 WHERE id = :saleId")
    suspend fun softDeleteSale(saleId: Int)
    
    @Query("UPDATE sales SET isDeleted = 0 WHERE id = :saleId")
    suspend fun restoreSale(saleId: Int)
    
    @Query("DELETE FROM sales WHERE id = :saleId")
    suspend fun permanentlyDeleteSale(saleId: Int)
    
    @Query("SELECT SUM(amount) FROM sales WHERE isDeleted = 0 AND date >= :startDate")
    suspend fun getTotalByDateRange(startDate: Long): Double?
    
    @Query("SELECT COUNT(*) FROM sales WHERE isDeleted = 0")
    suspend fun getActiveCount(): Int
}
