package com.hninakari.saletracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hninakari.saletracker.data.model.ExpenseCategory

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val category: String,  // Store as string
    val type: String = "BUSINESS",  // NEW: BUSINESS or PERSONAL
    val description: String = "",
    val date: Long,
    val isDeleted: Boolean = false
)
