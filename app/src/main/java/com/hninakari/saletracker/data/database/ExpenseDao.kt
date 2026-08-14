package com.hninakari.saletracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE isDeleted = 0 ORDER BY date DESC")
    fun getActiveExpenses(): Flow<List<ExpenseEntity>>
    
    @Query("SELECT * FROM expenses WHERE isDeleted = 0 AND date >= :startDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Long): Flow<List<ExpenseEntity>>
    
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>
    
    @Insert
    suspend fun insertExpense(expense: ExpenseEntity)
    
    @Update
    suspend fun updateExpense(expense: ExpenseEntity)
    
    @Query("UPDATE expenses SET isDeleted = 1 WHERE id = :expenseId")
    suspend fun softDeleteExpense(expenseId: Int)
    
    @Query("UPDATE expenses SET isDeleted = 0 WHERE id = :expenseId")
    suspend fun restoreExpense(expenseId: Int)
    
    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun permanentlyDeleteExpense(expenseId: Int)
    
    @Query("SELECT SUM(amount) FROM expenses WHERE isDeleted = 0 AND date >= :startDate")
    suspend fun getTotalByDateRange(startDate: Long): Double?
    
    @Query("SELECT COUNT(*) FROM expenses WHERE isDeleted = 0")
    suspend fun getActiveCount(): Int
}
