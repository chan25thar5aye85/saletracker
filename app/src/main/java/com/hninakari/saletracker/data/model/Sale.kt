package com.hninakari.saletracker.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class PaymentType {
    CASH,
    KPAY,
    WAVEPAY
}

@Serializable
data class Sale(
    val id: Int = 0,
    val amount: Double,
    val paymentType: PaymentType,
    val date: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
