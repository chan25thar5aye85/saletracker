package com.hninakari.saletracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {
    @Query("SELECT * FROM debts WHERE isDeleted = 0 AND isPaid = 0 ORDER BY date DESC")
    fun getActiveDebts(): Flow<List<DebtEntity>>
    
    @Query("SELECT * FROM debts WHERE isDeleted = 0 ORDER BY date DESC")
    fun getAllDebts(): Flow<List<DebtEntity>>
    
    @Query("SELECT * FROM debts WHERE isDeleted = 0 AND personId = :personId ORDER BY date DESC")
    fun getDebtsByPerson(personId: Int): Flow<List<DebtEntity>>
    
    @Query("SELECT * FROM debts WHERE isDeleted = 0 AND personId = :personId AND isPaid = 0")
    fun getActiveDebtsByPerson(personId: Int): Flow<List<DebtEntity>>
    
    @Query("SELECT * FROM debts WHERE isDeleted = 0 AND personId = :personId AND isPaid = 1")
    fun getPaidDebtsByPerson(personId: Int): Flow<List<DebtEntity>>
    
    @Insert
    suspend fun insertDebt(debt: DebtEntity)
    
    @Update
    suspend fun updateDebt(debt: DebtEntity)
    
    @Query("UPDATE debts SET isPaid = 1 WHERE id = :debtId")
    suspend fun markDebtAsPaid(debtId: Int)
    
    @Query("UPDATE debts SET isDeleted = 1 WHERE id = :debtId")
    suspend fun softDeleteDebt(debtId: Int)
    
    @Query("SELECT * FROM debt_payments WHERE isDeleted = 0 AND debtId = :debtId ORDER BY date DESC")
    fun getPaymentsForDebt(debtId: Int): Flow<List<DebtPaymentEntity>>
    
    @Query("SELECT * FROM debt_payments WHERE isDeleted = 0 ORDER BY date DESC")
    fun getAllPayments(): Flow<List<DebtPaymentEntity>>
    
    @Insert
    suspend fun insertPayment(payment: DebtPaymentEntity)
    
    @Query("UPDATE debt_payments SET isDeleted = 1 WHERE id = :paymentId")
    suspend fun softDeletePayment(paymentId: Int)
    
    @Query("SELECT SUM(amount) FROM debt_payments WHERE isDeleted = 0 AND debtId = :debtId")
    suspend fun getTotalPaidForDebt(debtId: Int): Double?
}
