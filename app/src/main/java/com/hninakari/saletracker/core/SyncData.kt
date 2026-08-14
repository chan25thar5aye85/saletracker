package com.hninakari.saletracker.core

import com.hninakari.saletracker.data.model.*
import kotlinx.serialization.Serializable

@Serializable
data class SyncData(
    val sales: List<Sale> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val transfers: List<Transfer> = emptyList(),
    val people: List<Person> = emptyList(),
    val debts: List<Debt> = emptyList(),
    val debtPayments: List<DebtPayment> = emptyList(),
    val products: List<Product> = emptyList(),
    val toBuyItems: List<ToBuyItem> = emptyList(),
    val purchaseBatches: List<PurchaseBatch> = emptyList(),
    val purchaseBatchItems: List<PurchaseBatchItem> = emptyList(),
    val orders: List<Order> = emptyList(),
    val orderItems: List<OrderItem> = emptyList()
)

@Serializable
data class SyncRecord(
    val user_id: String,
    val data: SyncData,
    val updated_at: Long
)

@Serializable
data class SyncDataResponse(
    val user_id: String,
    val data: SyncData,
    val updated_at: Long,
    val created_at: String? = null
)
