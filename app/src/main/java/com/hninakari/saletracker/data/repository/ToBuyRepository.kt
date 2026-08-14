package com.hninakari.saletracker.data.repository

import android.content.Context
import com.hninakari.saletracker.core.SyncTrigger
import com.hninakari.saletracker.data.database.PurchaseBatchEntity
import com.hninakari.saletracker.data.database.PurchaseBatchItemEntity
import com.hninakari.saletracker.data.database.PurchaseDatabase
import com.hninakari.saletracker.data.database.ToBuyItemEntity
import com.hninakari.saletracker.data.model.Priority
import com.hninakari.saletracker.data.model.PurchaseBatch
import com.hninakari.saletracker.data.model.PurchaseBatchItem
import com.hninakari.saletracker.data.model.ToBuyItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ToBuyRepository(context: Context) {

    private val database = PurchaseDatabase.getDatabase(context)

    private val toBuyItemDao = database.toBuyItemDao()
    private val purchaseBatchDao = database.purchaseBatchDao()
    private val purchaseBatchItemDao = database.purchaseBatchItemDao()
    private val productDao = database.productDao()

    private fun toEntity(item: ToBuyItem): ToBuyItemEntity {
        return ToBuyItemEntity(
            id = item.id,
            productId = item.productId,
            quantity = item.quantity,
            priority = item.priority.name,
            note = item.note,
            dateAdded = item.dateAdded,
            isBought = item.isBought,
            isDeleted = item.isDeleted
        )
    }

    private fun toModel(entity: ToBuyItemEntity): ToBuyItem {
        return ToBuyItem(
            id = entity.id,
            productId = entity.productId,
            quantity = entity.quantity,
            priority = Priority.valueOf(entity.priority),
            note = entity.note,
            dateAdded = entity.dateAdded,
            isBought = entity.isBought,
            isDeleted = entity.isDeleted
        )
    }

    // ---------------------------------------------------------
    // To Buy Items
    // ---------------------------------------------------------

    fun getActiveToBuyItems(): Flow<List<ToBuyItem>> {
        return toBuyItemDao
            .getActiveToBuyItems()
            .map { entities ->
                entities.map { toModel(it) }
            }
    }

    fun getBoughtItems(): Flow<List<ToBuyItem>> {
        return toBuyItemDao
            .getBoughtToBuyItems()
            .map { entities ->
                entities.map { toModel(it) }
            }
    }

    fun getAllToBuyItems(): Flow<List<ToBuyItem>> {
        return toBuyItemDao
            .getAllToBuyItems()
            .map { entities ->
                entities.map { toModel(it) }
            }
    }

    suspend fun addToBuyItem(item: ToBuyItem): Long {

        val id = toBuyItemDao.insertToBuyItem(
            toEntity(item)
        )

        SyncTrigger.triggerUpload()

        return id
    }

    suspend fun updateToBuyItem(item: ToBuyItem) {

        toBuyItemDao.updateToBuyItem(
            toEntity(item)
        )

        SyncTrigger.triggerUpload()
    }

    suspend fun markAsBought(itemId: Int) {

        toBuyItemDao.markAsBought(itemId)

        SyncTrigger.triggerUpload()
    }

    suspend fun deleteToBuyItem(itemId: Int) {

        toBuyItemDao.softDeleteToBuyItem(itemId)

        SyncTrigger.triggerUpload()
    }

    suspend fun getActiveCount(): Int {
        return toBuyItemDao.getActiveCount()
    }

    suspend fun getTotalQuantity(): Int {
        return toBuyItemDao.getTotalQuantity() ?: 0
    }

    // ---------------------------------------------------------
    // Purchase Batches
    // ---------------------------------------------------------

    private fun toBatchEntity(
        batch: PurchaseBatch
    ): PurchaseBatchEntity {

        return PurchaseBatchEntity(
            id = batch.id,
            supplierPersonId = batch.supplierPersonId,
            date = batch.date,
            totalAmount = batch.totalAmount,
            note = batch.note,
            expenseId = batch.expenseId,
            isDeleted = batch.isDeleted
        )
    }

    private fun toBatchModel(
        entity: PurchaseBatchEntity
    ): PurchaseBatch {

        return PurchaseBatch(
            id = entity.id,
            supplierPersonId = entity.supplierPersonId,
            date = entity.date,
            totalAmount = entity.totalAmount,
            note = entity.note,
            expenseId = entity.expenseId,
            isDeleted = entity.isDeleted
        )
    }

    private fun toBatchItemEntity(
        item: PurchaseBatchItem
    ): PurchaseBatchItemEntity {

        return PurchaseBatchItemEntity(
            id = item.id,
            batchId = item.batchId,
            productId = item.productId,
            quantity = item.quantity,
            unitPrice = item.unitPrice,
            totalPrice = item.totalPrice,
            isDeleted = item.isDeleted
        )
    }

    private fun toBatchItemModel(
        entity: PurchaseBatchItemEntity
    ): PurchaseBatchItem {

        return PurchaseBatchItem(
            id = entity.id,
            batchId = entity.batchId,
            productId = entity.productId,
            quantity = entity.quantity,
            unitPrice = entity.unitPrice,
            totalPrice = entity.totalPrice,
            isDeleted = entity.isDeleted
        )
    }

    fun getAllBatches(): Flow<List<PurchaseBatch>> {
        return purchaseBatchDao
            .getAllBatches()
            .map { entities ->
                entities.map { toBatchModel(it) }
            }
    }

    suspend fun getBatchById(
        batchId: Int
    ): PurchaseBatch? {

        val entity =
            purchaseBatchDao.getBatchById(batchId)

        return entity?.let {
            toBatchModel(it)
        }
    }

    fun getItemsForBatch(
        batchId: Int
    ): Flow<List<PurchaseBatchItem>> {

        return purchaseBatchItemDao
            .getItemsForBatch(batchId)
            .map { entities ->
                entities.map {
                    toBatchItemModel(it)
                }
            }
    }

    suspend fun createPurchaseBatch(
        supplierPersonId: Int?,
        note: String,
        itemIds: List<Int>
    ): Long {

        val items =
            toBuyItemDao
                .getActiveToBuyItems()
                .first()
                .filter { it.id in itemIds }

        if (items.isEmpty()) {
            return -1
        }

        val productIds =
            items.map { it.productId }

        val products =
            productDao
                .getAllProducts()
                .first()
                .filter { it.id in productIds }

        val batchItems =
            items.map { item ->

                val product =
                    products.find {
                        it.id == item.productId
                    }

                val unitPrice =
                    product?.price ?: 0.0

                PurchaseBatchItem(
                    batchId = 0,
                    productId = item.productId,
                    quantity = item.quantity,
                    unitPrice = unitPrice,
                    totalPrice =
                        unitPrice * item.quantity
                )
            }

        val total =
            batchItems.sumOf {
                it.totalPrice
            }

        val batch =
            PurchaseBatch(
                supplierPersonId = supplierPersonId,
                totalAmount = total,
                note = note,
                date = System.currentTimeMillis()
            )

        val batchId =
            purchaseBatchDao.insertBatch(
                toBatchEntity(batch)
            )

        val itemEntities =
            batchItems.map {
                toBatchItemEntity(
                    it.copy(
                        batchId = batchId.toInt()
                    )
                )
            }

        purchaseBatchItemDao.insertBatchItems(
            itemEntities
        )

        items.forEach { item ->
            toBuyItemDao.markAsBought(item.id)
        }

        SyncTrigger.triggerUpload()

        return batchId
    }

    suspend fun deleteBatch(
        batchId: Int
    ) {

        purchaseBatchDao.softDeleteBatch(
            batchId
        )

        purchaseBatchItemDao.softDeleteByBatch(
            batchId
        )

        SyncTrigger.triggerUpload()
    }
}
