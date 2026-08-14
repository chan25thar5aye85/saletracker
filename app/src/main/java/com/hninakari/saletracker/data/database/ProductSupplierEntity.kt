package com.hninakari.saletracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_suppliers")
data class ProductSupplierEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int = 0,
    val supplierPersonId: Int = 0,
    val price: Double = 0.0,
    val isDefault: Boolean = false,
    val isDeleted: Boolean = false
)
