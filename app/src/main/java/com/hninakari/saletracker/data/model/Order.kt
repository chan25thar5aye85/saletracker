package com.hninakari.saletracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Int = 0,
    val supplierPersonId: Int? = null,
    val date: Long = System.currentTimeMillis(),
    val totalAmount: Double = 0.0,
    val note: String = "",
    val status: String = "draft",
    val expenseId: Int? = null,
    val isDeleted: Boolean = false
)

@Serializable
data class OrderItem(
    val id: Int = 0,
    val orderId: Int = 0,
    val productId: Int = 0,
    val quantity: Int = 0,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val isDeleted: Boolean = false
)
