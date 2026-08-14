package com.hninakari.saletracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val supplierPersonId: Int? = null,
    val date: Long = System.currentTimeMillis(),
    val totalAmount: Double = 0.0,
    val note: String = "",
    val status: String = "draft", // draft, purchased, cancelled
    val expenseId: Int? = null,
    val isDeleted: Boolean = false
)
