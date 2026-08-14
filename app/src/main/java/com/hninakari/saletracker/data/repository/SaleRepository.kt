package com.hninakari.saletracker.data.repository

import android.content.Context
import com.hninakari.saletracker.core.SyncTrigger
import com.hninakari.saletracker.data.database.AppDatabase
import com.hninakari.saletracker.data.database.SaleEntity
import com.hninakari.saletracker.data.model.PaymentType
import com.hninakari.saletracker.data.model.Sale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SaleRepository(context: Context) {

    private val database = AppDatabase.getDatabase(context)
    private val saleDao = database.saleDao()

    private fun toEntity(sale: Sale): SaleEntity {
        return SaleEntity(
            id = sale.id,
            amount = sale.amount,
            paymentType = sale.paymentType.name,
            date = sale.date,
            isDeleted = sale.isDeleted
        )
    }

    private fun toModel(entity: SaleEntity): Sale {
        return Sale(
            id = entity.id,
            amount = entity.amount,
            paymentType = PaymentType.valueOf(entity.paymentType),
            date = entity.date,
            isDeleted = entity.isDeleted
        )
    }

    fun getActiveSales(): Flow<List<Sale>> {
        return saleDao
            .getActiveSales()
            .map { entities ->
                entities.map { toModel(it) }
            }
    }

    fun getSalesByDateRange(
        startDate: Long
    ): Flow<List<Sale>> {
        return saleDao
            .getSalesByDateRange(startDate)
            .map { entities ->
                entities.map { toModel(it) }
            }
    }

    fun getAllSales(): Flow<List<Sale>> {
        return saleDao
            .getAllSales()
            .map { entities ->
                entities.map { toModel(it) }
            }
    }

    suspend fun addSale(sale: Sale) {
        saleDao.insertSale(toEntity(sale))
        SyncTrigger.triggerUpload()
    }

    suspend fun updateSale(sale: Sale) {
        saleDao.updateSale(toEntity(sale))
        SyncTrigger.triggerUpload()
    }

    suspend fun deleteSale(saleId: Int) {
        saleDao.softDeleteSale(saleId)
        SyncTrigger.triggerUpload()
    }

    suspend fun restoreSale(saleId: Int) {
        saleDao.restoreSale(saleId)
        SyncTrigger.triggerUpload()
    }

    suspend fun getTotalByDateRange(
        startDate: Long
    ): Double {
        return saleDao
            .getTotalByDateRange(startDate)
            ?: 0.0
    }

    suspend fun getActiveCount(): Int {
        return saleDao.getActiveCount()
    }
}
