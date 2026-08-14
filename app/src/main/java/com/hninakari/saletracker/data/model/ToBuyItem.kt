package com.hninakari.saletracker.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class Priority {
    HIGH, MEDIUM, LOW
}

@Serializable
data class ToBuyItem(
    val id: Int = 0,
    val productId: Int,
    val quantity: Int = 1,
    val priority: Priority = Priority.MEDIUM,
    val note: String = "",
    val dateAdded: Long = System.currentTimeMillis(),
    val isBought: Boolean = false,
    val isDeleted: Boolean = false
)
