package com.hninakari.saletracker.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class PersonType {
    CUSTOMER, SUPPLIER, OTHER
}

@Serializable
data class Person(
    val id: Int = 0,
    val name: String,
    val phone: String = "",
    val type: PersonType = PersonType.OTHER,
    val notes: String = "",
    val dateAdded: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
)
