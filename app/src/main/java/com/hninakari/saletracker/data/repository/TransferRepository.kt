package com.hninakari.saletracker.data.repository

import android.content.Context
import com.hninakari.saletracker.core.SyncTrigger
import com.hninakari.saletracker.data.database.AppDatabase
import com.hninakari.saletracker.data.database.TransferEntity
import com.hninakari.saletracker.data.model.Transfer
import com.hninakari.saletracker.data.model.TransferDirection
import com.hninakari.saletracker.data.model.TransferService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransferRepository(context: Context) {

    private val database =
        AppDatabase.getDatabase(context)

    private val transferDao =
        database.transferDao()

    private fun toEntity(
        transfer: Transfer
    ): TransferEntity {
        return TransferEntity(
            id = transfer.id,
            service = transfer.service.name,
            direction = transfer.direction.name,
            amount = transfer.amount,
            fee = transfer.fee,
            customerName = transfer.customerName,
            customerPhone = transfer.customerPhone,
            notes = transfer.notes,
            date = transfer.date,
            isDeleted = transfer.isDeleted
        )
    }

    private fun toModel(
        entity: TransferEntity
    ): Transfer {
        return Transfer(
            id = entity.id,
            service = TransferService.valueOf(
                entity.service
            ),
            direction = TransferDirection.valueOf(
                entity.direction
            ),
            amount = entity.amount,
            fee = entity.fee,
            customerName = entity.customerName,
            customerPhone = entity.customerPhone,
            notes = entity.notes,
            date = entity.date,
            isDeleted = entity.isDeleted
        )
    }

    fun getActiveTransfers(): Flow<List<Transfer>> {
        return transferDao
            .getActiveTransfers()
            .map { entities ->
                entities.map {
                    toModel(it)
                }
            }
    }

    fun getTransfersByDateRange(
        startDate: Long
    ): Flow<List<Transfer>> {

        return transferDao
            .getTransfersByDateRange(startDate)
            .map { entities ->
                entities.map {
                    toModel(it)
                }
            }
    }

    fun getAllTransfers(): Flow<List<Transfer>> {
        return transferDao
            .getAllTransfers()
            .map { entities ->
                entities.map {
                    toModel(it)
                }
            }
    }

    suspend fun addTransfer(
        transfer: Transfer
    ) {

        // Save locally first
        transferDao.insertTransfer(
            toEntity(transfer)
        )

        // Notify sync system
        SyncTrigger.triggerUpload()
    }

    suspend fun updateTransfer(
        transfer: Transfer
    ) {

        // Update locally first
        transferDao.updateTransfer(
            toEntity(transfer)
        )

        // Notify sync system
        SyncTrigger.triggerUpload()
    }

    suspend fun deleteTransfer(
        transferId: Int
    ) {

        // Soft delete locally
        transferDao.softDeleteTransfer(
            transferId
        )

        // Notify sync system
        SyncTrigger.triggerUpload()
    }

    suspend fun getTotalFeesByDateRange(
        startDate: Long
    ): Double {

        return transferDao
            .getTotalFeesByDateRange(startDate)
            ?: 0.0
    }
}
