package com.hninakari.saletracker.data.repository

import android.content.Context
import com.hninakari.saletracker.core.SyncTrigger
import com.hninakari.saletracker.data.database.AppDatabase
import com.hninakari.saletracker.data.database.ExpenseEntity
import com.hninakari.saletracker.data.model.Expense
import com.hninakari.saletracker.data.model.ExpenseCategory
import com.hninakari.saletracker.data.model.ExpenseType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepository(context: Context) {

    private val database = AppDatabase.getDatabase(context)
    private val expenseDao = database.expenseDao()

    private fun toEntity(expense: Expense): ExpenseEntity {
        return ExpenseEntity(
            id = expense.id,
            amount = expense.amount,
            category = expense.category.name,
            type = expense.type.name,
            description = expense.description,
            date = expense.date,
            isDeleted = expense.isDeleted
        )
    }

    private fun toModel(entity: ExpenseEntity): Expense {
        return Expense(
            id = entity.id,
            amount = entity.amount,
            category = ExpenseCategory.valueOf(entity.category),
            type = ExpenseType.valueOf(entity.type),
            description = entity.description,
            date = entity.date,
            isDeleted = entity.isDeleted
        )
    }

    fun getActiveExpenses(): Flow<List<Expense>> {
        return expenseDao.getActiveExpenses().map { entities ->
            entities.map { toModel(it) }
        }
    }

    fun getExpensesByDateRange(startDate: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesByDateRange(startDate).map { entities ->
            entities.map { toModel(it) }
        }
    }

    fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses().map { entities ->
            entities.map { toModel(it) }
        }
    }

    suspend fun addExpense(expense: Expense) {
        expenseDao.insertExpense(toEntity(expense))
        SyncTrigger.triggerUpload()
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(toEntity(expense))
        SyncTrigger.triggerUpload()
    }

    suspend fun deleteExpense(expenseId: Int) {
        expenseDao.softDeleteExpense(expenseId)
        SyncTrigger.triggerUpload()
    }

    suspend fun restoreExpense(expenseId: Int) {
        expenseDao.restoreExpense(expenseId)
        SyncTrigger.triggerUpload()
    }

    suspend fun getTotalByDateRange(startDate: Long): Double {
        return expenseDao.getTotalByDateRange(startDate) ?: 0.0
    }

    suspend fun getActiveCount(): Int {
        return expenseDao.getActiveCount()
    }
}
