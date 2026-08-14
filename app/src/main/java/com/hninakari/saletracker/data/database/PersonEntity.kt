package com.hninakari.saletracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hninakari.saletracker.data.model.PersonType

@Entity(tableName = "people")
data class PersonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val phone: String = "",
    val type: String,  // CUSTOMER, SUPPLIER, OTHER
    val notes: String = "",
    val dateAdded: Long,
    val isDeleted: Boolean = false
)
