package com.hninakari.saletracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transfers")
data class TransferEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val service: String,  // KPAY or WAVEPAY
    val direction: String,  // IN or OUT
    val amount: Double,
    val fee: Double,
    val customerName: String = "",
    val customerPhone: String = "",
    val notes: String = "",
    val date: Long,
    val isDeleted: Boolean = false
)
