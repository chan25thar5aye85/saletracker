package com.hninakari.saletracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int = 0,
    val name: String,
    val price: Double,
    val isDeleted: Boolean = false
)
