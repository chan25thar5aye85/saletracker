package com.hninakari.saletracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "to_buy_items")
data class ToBuyItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int = 0,
    val quantity: Int = 1,
    val priority: String = "MEDIUM",
    val note: String = "",
    val dateAdded: Long = System.currentTimeMillis(),
    val orderId: Int? = null, // Link to order when added to order list
    val isBought: Boolean = false,
    val isDeleted: Boolean = false
)
