package com.hninakari.saletracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val personId: Int,
    val type: String,  // OWED_TO_ME or I_OWE
    val amount: Double,  // Current remaining
    val originalAmount: Double = 0.0,  // Original total
    val note: String = "",
    val date: Long,
    val isPaid: Boolean = false,
    val isDeleted: Boolean = false
)

@Entity(tableName = "debt_payments")
data class DebtPaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val debtId: Int,
    val amount: Double,
    val date: Long,
    val note: String = "",
    val isDeleted: Boolean = false
)
