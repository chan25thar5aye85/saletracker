package com.hninakari.saletracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseBatchItem(
    val id: Int = 0,
    val batchId: Int,
    val productId: Int,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double,
    val isDeleted: Boolean = false
)
