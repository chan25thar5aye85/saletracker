package com.hninakari.saletracker.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class TransferService {
    KPAY, WAVEPAY
}

@Serializable
enum class TransferDirection {
    IN, OUT
}

@Serializable
data class Transfer(
    val id: Int = 0,
    val service: TransferService,
    val direction: TransferDirection,
    val amount: Double,
    val fee: Double = 0.0,
    val customerName: String = "",
    val customerPhone: String = "",
    val notes: String = "",
    val date: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) {
    val totalAmount: Double
        get() = when (direction) {
            TransferDirection.IN -> amount - fee
            TransferDirection.OUT -> amount + fee
        }
}
