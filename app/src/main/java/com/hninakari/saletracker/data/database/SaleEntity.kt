package com.hninakari.saletracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hninakari.saletracker.data.model.PaymentType

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val paymentType: String,  // Store as string
    val date: Long,
    val isDeleted: Boolean = false
)
