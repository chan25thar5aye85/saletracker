package com.hninakari.saletracker.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class DebtType {
    OWED_TO_ME, I_OWE
}

@Serializable
data class Debt(
    val id: Int = 0,
    val personId: Int,
    val type: DebtType,
    val amount: Double,
    val originalAmount: Double = 0.0,
    val note: String = "",
    val date: Long = System.currentTimeMillis(),
    val isPaid: Boolean = false,
    val isDeleted: Boolean = false
)

@Serializable
data class DebtPayment(
    val id: Int = 0,
    val debtId: Int,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val note: String = "",
    val isDeleted: Boolean = false
)
