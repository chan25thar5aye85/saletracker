package com.hninakari.saletracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchase_batch_items")
data class PurchaseBatchItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val batchId: Int = 0,
    val productId: Int = 0,
    val quantity: Int = 0,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val isDeleted: Boolean = false
)
