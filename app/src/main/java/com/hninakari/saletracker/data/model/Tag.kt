package com.hninakari.saletracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val id: Int = 0,
    val name: String,
    val color: String? = null,
    val isDeleted: Boolean = false
)
