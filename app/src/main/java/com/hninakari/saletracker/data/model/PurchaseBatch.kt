package com.hninakari.saletracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseBatch(
    val id: Int = 0,
    val supplierPersonId: Int? = null,
    val date: Long = System.currentTimeMillis(),
    val totalAmount: Double = 0.0,
    val note: String = "",
    val expenseId: Int? = null,
    val isDeleted: Boolean = false
)
