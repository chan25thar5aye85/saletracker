package com.hninakari.saletracker.data.repository

import android.content.Context
import com.hninakari.saletracker.core.SyncTrigger
import com.hninakari.saletracker.data.database.AppDatabase
import com.hninakari.saletracker.data.database.DebtEntity
import com.hninakari.saletracker.data.database.DebtPaymentEntity
import com.hninakari.saletracker.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DebtRepository(context: Context) {

    private val database = AppDatabase.getDatabase(context)
    private val debtDao = database.debtDao()

    private fun toDebtEntity(debt: Debt): DebtEntity {
        return DebtEntity(
            id = debt.id,
            personId = debt.personId,
            type = debt.type.name,
            amount = debt.amount,
            originalAmount = debt.originalAmount,
            note = debt.note,
            date = debt.date,
            isPaid = debt.isPaid,
            isDeleted = debt.isDeleted
        )
    }

    private fun toDebt(entity: DebtEntity): Debt {
        return Debt(
            id = entity.id,
            personId = entity.personId,
            type = DebtType.valueOf(entity.type),
            amount = entity.amount,
            originalAmount = entity.originalAmount,
            note = entity.note,
            date = entity.date,
            isPaid = entity.isPaid,
            isDeleted = entity.isDeleted
        )
    }

    private fun toPaymentEntity(payment: DebtPayment): DebtPaymentEntity {
        return DebtPaymentEntity(
            id = payment.id,
            debtId = payment.debtId,
            amount = payment.amount,
            date = payment.date,
            note = payment.note,
            isDeleted = payment.isDeleted
        )
    }

    private fun toPayment(entity: DebtPaymentEntity): DebtPayment {
        return DebtPayment(
            id = entity.id,
            debtId = entity.debtId,
            amount = entity.amount,
            date = entity.date,
            note = entity.note,
            isDeleted = entity.isDeleted
        )
    }

    fun getAllDebts(): Flow<List<Debt>> {
        return debtDao.getAllDebts().map { entities ->
            entities.map { toDebt(it) }
        }
    }

    fun getActiveDebts(): Flow<List<Debt>> {
        return debtDao.getActiveDebts().map { entities ->
            entities.map { toDebt(it) }
        }
    }

    fun getDebtsByPerson(personId: Int): Flow<List<Debt>> {
        return debtDao.getDebtsByPerson(personId).map { entities ->
            entities.map { toDebt(it) }
        }
    }

    fun getActiveDebtsByPerson(personId: Int): Flow<List<Debt>> {
        return debtDao.getActiveDebtsByPerson(personId).map { entities ->
            entities.map { toDebt(it) }
        }
    }

    fun getPaidDebtsByPerson(personId: Int): Flow<List<Debt>> {
        return debtDao.getPaidDebtsByPerson(personId).map { entities ->
            entities.map { toDebt(it) }
        }
    }

    fun getPaymentsForDebt(debtId: Int): Flow<List<DebtPayment>> {
        return debtDao.getPaymentsForDebt(debtId).map { entities ->
            entities.map { toPayment(it) }
        }
    }

    fun getAllPayments(): Flow<List<DebtPayment>> {
        return debtDao.getAllPayments().map { entities ->
            entities.map { toPayment(it) }
        }
    }

    suspend fun addDebt(debt: Debt) {
        debtDao.insertDebt(toDebtEntity(debt))
        SyncTrigger.triggerUpload()
    }

    suspend fun addPayment(payment: DebtPayment) {
        debtDao.insertPayment(toPaymentEntity(payment))
        SyncTrigger.triggerUpload()
    }

    suspend fun updateDebt(debt: Debt) {
        debtDao.updateDebt(toDebtEntity(debt))
        SyncTrigger.triggerUpload()
    }

    suspend fun markDebtAsPaid(debtId: Int) {
        debtDao.markDebtAsPaid(debtId)
        SyncTrigger.triggerUpload()
    }

    suspend fun getTotalPaidForDebt(debtId: Int): Double {
        return debtDao.getTotalPaidForDebt(debtId) ?: 0.0
    }
}
