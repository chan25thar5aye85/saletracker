package com.hninakari.saletracker.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class ExpenseCategory {
    INVENTORY, RENT, SALARY, UTILITIES, TRANSPORT, MARKETING, MAINTENANCE, OTHER
}

@Serializable
enum class ExpenseType {
    BUSINESS, PERSONAL
}

@Serializable
data class Expense(
    val id: Int = 0,
    val amount: Double,
    val category: ExpenseCategory,
    val type: ExpenseType = ExpenseType.BUSINESS,
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
