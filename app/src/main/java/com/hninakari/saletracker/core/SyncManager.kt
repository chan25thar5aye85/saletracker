package com.hninakari.saletracker.core

import android.content.Context
import android.util.Log
import com.hninakari.saletracker.data.model.*
import com.hninakari.saletracker.data.repository.*
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

class SyncManager(
    private val context: Context,
    private val saleRepository: SaleRepository,
    private val expenseRepository: ExpenseRepository,
    private val transferRepository: TransferRepository,
    private val personRepository: PersonRepository,
    private val debtRepository: DebtRepository,
    private val productRepository: ProductRepository,
    private val toBuyRepository: ToBuyRepository,
    private val orderRepository: OrderRepository
) {

    private val tag = "SyncManager"

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private fun getUserId(): String {
        return UserPreferences
            .getInstance(context)
            .getUserId()
    }

    // ============================================================
    // UPLOAD
    // ============================================================

    suspend fun uploadData(): Result<String> {

        val userId = getUserId()

        return try {

            Log.d(
                tag,
                "========================================"
            )

            Log.d(
                tag,
                "UPLOAD - Starting for user: $userId"
            )

            // ----------------------------------------------------
            // Read all local data
            // ----------------------------------------------------

            val sales =
                saleRepository
                    .getAllSales()
                    .first()
                    .filter { !it.isDeleted }

            val expenses =
                expenseRepository
                    .getAllExpenses()
                    .first()
                    .filter { !it.isDeleted }

            val transfers =
                transferRepository
                    .getAllTransfers()
                    .first()
                    .filter { !it.isDeleted }

            val people =
                personRepository
                    .getAllPeople()
                    .first()
                    .filter { !it.isDeleted }

            val debts =
                debtRepository
                    .getAllDebts()
                    .first()
                    .filter { !it.isDeleted }

            val debtPayments =
                debtRepository
                    .getAllPayments()
                    .first()
                    .filter { !it.isDeleted }

            val products =
                productRepository
                    .getAllProducts()
                    .first()
                    .filter { !it.isDeleted }

            val toBuyItems =
                toBuyRepository
                    .getAllToBuyItems()
                    .first()
                    .filter { !it.isDeleted }

            val purchaseBatches =
                toBuyRepository
                    .getAllBatches()
                    .first()
                    .filter { !it.isDeleted }

            val orders =
                orderRepository
                    .getAllOrders()
                    .first()
                    .filter { !it.isDeleted }

            val orderItems =
                orderRepository
                    .getAllOrderItems()
                    .first()
                    .filter { !it.isDeleted }

            Log.d(
                tag,
                "LOCAL DATA:"
            )

            Log.d(
                tag,
                "Sales: ${sales.size}"
            )

            Log.d(
                tag,
                "Expenses: ${expenses.size}"
            )

            Log.d(
                tag,
                "Transfers: ${transfers.size}"
            )

            Log.d(
                tag,
                "People: ${people.size}"
            )

            Log.d(
                tag,
                "Debts: ${debts.size}"
            )

            Log.d(
                tag,
                "Debt Payments: ${debtPayments.size}"
            )

            Log.d(
                tag,
                "Products: ${products.size}"
            )

            Log.d(
                tag,
                "ToBuy: ${toBuyItems.size}"
            )

            Log.d(
                tag,
                "Purchase Batches: ${purchaseBatches.size}"
            )

            Log.d(
                tag,
                "Orders: ${orders.size}"
            )

            Log.d(
                tag,
                "Order Items: ${orderItems.size}"
            )

            // ----------------------------------------------------
            // Create SyncData
            // ----------------------------------------------------

            val syncData = SyncData(

                sales = sales,

                expenses = expenses,

                transfers = transfers,

                people = people,

                debts = debts,

                debtPayments = debtPayments,

                products = products,

                toBuyItems = toBuyItems,

                purchaseBatches = purchaseBatches,

                /*
                 * We currently don't have a repository method
                 * for loading purchase batch items.
                 */
                purchaseBatchItems = emptyList(),

                orders = orders,

                orderItems = orderItems
            )

            // ----------------------------------------------------
            // Create sync record
            // ----------------------------------------------------

            val syncRecord = SyncRecord(

                user_id = userId,

                data = syncData,

                updated_at =
                    System.currentTimeMillis()
            )

            Log.d(
                tag,
                "Uploading sync_data..."
            )

            // ----------------------------------------------------
            // Upload to Supabase
            // ----------------------------------------------------

           SupabaseClient.from("sync_data").upsert(syncRecord) {
				onConflict = "user_id"
			}

            Log.d(
                tag,
                "✅ UPLOAD COMPLETE"
            )

            Log.d(
                tag,
                "User: $userId"
            )

            Log.d(
                tag,
                "Sales uploaded: ${sales.size}"
            )

            Log.d(
                tag,
                "========================================"
            )

            Result.success(userId)

        } catch (e: Exception) {

            Log.e(
                tag,
                "❌ UPLOAD FAILED",
                e
            )

            Result.failure(e)
        }
    }

    // ============================================================
    // DOWNLOAD
    // ============================================================

    suspend fun downloadData(): Result<SyncData> {

        val userId = getUserId()

        return try {

            Log.d(
                tag,
                "========================================"
            )

            Log.d(
                tag,
                "DOWNLOAD - Starting for user: $userId"
            )

            // ----------------------------------------------------
            // Get user's record
            // ----------------------------------------------------

            val responses =
                SupabaseClient
                    .from("sync_data")
                    .select {
                        filter {
                            eq(
                                "user_id",
                                userId
                            )
                        }
                    }
                    .decodeAs<List<SyncDataResponse>>()

            val response =
                responses.firstOrNull()

            if (response == null) {

                Log.d(
                    tag,
                    "No cloud data found for user: $userId"
                )

                return Result.success(
                    SyncData()
                )
            }

            Log.d(
                tag,
                "Cloud data found"
            )

            Log.d(
                tag,
                "Cloud sales: ${response.data.sales.size}"
            )

            Log.d(
                tag,
                "Cloud expenses: ${response.data.expenses.size}"
            )

            Log.d(
                tag,
                "Cloud people: ${response.data.people.size}"
            )

            Log.d(
                tag,
                "Cloud debts: ${response.data.debts.size}"
            )

            Log.d(
                tag,
                "Cloud products: ${response.data.products.size}"
            )

            // ----------------------------------------------------
            // Save cloud data locally
            //
            // IMPORTANT:
            // Disable automatic upload while doing this.
            // ----------------------------------------------------

            SyncTrigger.setSuppressed(true)

            try {

                saveAllDataToLocal(
                    response.data
                )

            } finally {

                SyncTrigger.setSuppressed(false)
            }

            Log.d(
                tag,
                "✅ DOWNLOAD COMPLETE"
            )

            Log.d(
                tag,
                "========================================"
            )

            Result.success(
                response.data
            )

        } catch (e: Exception) {

            Log.e(
                tag,
                "❌ DOWNLOAD FAILED",
                e
            )

            Result.failure(e)
        }
    }

    // ============================================================
    // SAVE CLOUD DATA TO LOCAL DATABASE
    // ============================================================

    private suspend fun saveAllDataToLocal(
        data: SyncData
    ) {

        try {

            Log.d(
                tag,
                "Saving cloud data locally..."
            )

            // ----------------------------------------------------
            // Load existing data ONCE
            // ----------------------------------------------------

            val existingSales =
                saleRepository
                    .getAllSales()
                    .first()
                    .associateBy { it.id }

            val existingExpenses =
                expenseRepository
                    .getAllExpenses()
                    .first()
                    .associateBy { it.id }

            val existingTransfers =
                transferRepository
                    .getAllTransfers()
                    .first()
                    .associateBy { it.id }

            val existingPeople =
                personRepository
                    .getAllPeople()
                    .first()
                    .associateBy { it.id }

            val existingDebts =
                debtRepository
                    .getAllDebts()
                    .first()
                    .associateBy { it.id }

            val existingPayments =
                debtRepository
                    .getAllPayments()
                    .first()
                    .associateBy { it.id }

            val existingProducts =
                productRepository
                    .getAllProducts()
                    .first()
                    .associateBy { it.id }

            val existingToBuy =
                toBuyRepository
                    .getAllToBuyItems()
                    .first()
                    .associateBy { it.id }

            val existingOrders =
                orderRepository
                    .getAllOrders()
                    .first()
                    .associateBy { it.id }

            // ====================================================
            // SALES
            // ====================================================

            data.sales.forEach { sale ->

                try {

                    if (existingSales.containsKey(sale.id)) {

                        saleRepository.updateSale(
                            sale
                        )

                        Log.d(
                            tag,
                            "Updated sale: ${sale.id}"
                        )

                    } else {

                        saleRepository.addSale(
                            sale
                        )

                        Log.d(
                            tag,
                            "Added sale: ${sale.id}"
                        )
                    }

                } catch (e: Exception) {

                    Log.e(
                        tag,
                        "Failed to save sale ${sale.id}",
                        e
                    )
                }
            }

            // ====================================================
            // EXPENSES
            // ====================================================

            data.expenses.forEach { expense ->

                try {

                    if (existingExpenses.containsKey(expense.id)) {

                        expenseRepository.updateExpense(
                            expense
                        )

                        Log.d(
                            tag,
                            "Updated expense: ${expense.id}"
                        )

                    } else {

                        expenseRepository.addExpense(
                            expense
                        )

                        Log.d(
                            tag,
                            "Added expense: ${expense.id}"
                        )
                    }

                } catch (e: Exception) {

                    Log.e(
                        tag,
                        "Failed to save expense ${expense.id}",
                        e
                    )
                }
            }

            // ====================================================
            // PEOPLE
            // ====================================================

            data.people.forEach { person ->

                try {

                    if (existingPeople.containsKey(person.id)) {

                        personRepository.updatePerson(
                            person
                        )

                        Log.d(
                            tag,
                            "Updated person: ${person.id}"
                        )

                    } else {

                        personRepository.addPerson(
                            person
                        )

                        Log.d(
                            tag,
                            "Added person: ${person.id}"
                        )
                    }

                } catch (e: Exception) {

                    Log.e(
                        tag,
                        "Failed to save person ${person.id}",
                        e
                    )
                }
            }

            // ====================================================
            // DEBTS
            // ====================================================

            data.debts.forEach { debt ->

                try {

                    if (existingDebts.containsKey(debt.id)) {

                        debtRepository.updateDebt(
                            debt
                        )

                        Log.d(
                            tag,
                            "Updated debt: ${debt.id}"
                        )

                    } else {

                        debtRepository.addDebt(
                            debt
                        )

                        Log.d(
                            tag,
                            "Added debt: ${debt.id}"
                        )
                    }

                } catch (e: Exception) {

                    Log.e(
                        tag,
                        "Failed to save debt ${debt.id}",
                        e
                    )
                }
            }

            // ====================================================
            // DEBT PAYMENTS
            // ====================================================

            data.debtPayments.forEach { payment ->

                try {

                    if (!existingPayments.containsKey(payment.id)) {

                        debtRepository.addPayment(
                            payment
                        )

                        Log.d(
                            tag,
                            "Added debt payment: ${payment.id}"
                        )

                    } else {

                        /*
                         * Your current DebtRepository does not
                         * expose updatePayment(), so don't call it.
                         *
                         * Existing payments are left unchanged.
                         */
                        Log.d(
                            tag,
                            "Payment already exists: ${payment.id}"
                        )
                    }

                } catch (e: Exception) {

                    Log.e(
                        tag,
                        "Failed to save debt payment ${payment.id}",
                        e
                    )
                }
            }

            // ====================================================
            // PRODUCTS
            // ====================================================

            data.products.forEach { product ->

                try {

                    if (existingProducts.containsKey(product.id)) {

                        productRepository.updateProduct(
                            product
                        )

                        Log.d(
                            tag,
                            "Updated product: ${product.id}"
                        )

                    } else {

                        productRepository.addProduct(
                            product
                        )

                        Log.d(
                            tag,
                            "Added product: ${product.id}"
                        )
                    }

                } catch (e: Exception) {

                    Log.e(
                        tag,
                        "Failed to save product ${product.id}",
                        e
                    )
                }
            }

            // ====================================================
            // TO-BUY ITEMS
            // ====================================================

            data.toBuyItems.forEach { item ->

                try {

                    if (existingToBuy.containsKey(item.id)) {

                        toBuyRepository.updateToBuyItem(
                            item
                        )

                        Log.d(
                            tag,
                            "Updated toBuy item: ${item.id}"
                        )

                    } else {

                        toBuyRepository.addToBuyItem(
                            item
                        )

                        Log.d(
                            tag,
                            "Added toBuy item: ${item.id}"
                        )
                    }

                } catch (e: Exception) {

                    Log.e(
                        tag,
                        "Failed to save toBuy item ${item.id}",
                        e
                    )
                }
            }

            // ====================================================
            // PURCHASE BATCHES
            // ====================================================

            data.purchaseBatches.forEach { batch ->

                /*
                 * Your current ToBuyRepository does not appear
                 * to expose add/update methods for PurchaseBatch.
                 *
                 * We leave these alone for now.
                 */

                Log.d(
                    tag,
                    "PurchaseBatch received: ${batch.id}"
                )
            }

            // ====================================================
            // TRANSFERS
            // ====================================================

            data.transfers.forEach { transfer ->

                try {

                    if (existingTransfers.containsKey(transfer.id)) {

                        transferRepository.updateTransfer(
                            transfer
                        )

                        Log.d(
                            tag,
                            "Updated transfer: ${transfer.id}"
                        )

                    } else {

                        transferRepository.addTransfer(
                            transfer
                        )

                        Log.d(
                            tag,
                            "Added transfer: ${transfer.id}"
                        )
                    }

                } catch (e: Exception) {

                    Log.e(
                        tag,
                        "Failed to save transfer ${transfer.id}",
                        e
                    )
                }
            }

            // ====================================================
            // ORDERS
            // ====================================================

            data.orders.forEach { order ->

                try {

                    if (existingOrders.containsKey(order.id)) {

                        orderRepository.updateOrder(
                            order
                        )

                        Log.d(
                            tag,
                            "Updated order: ${order.id}"
                        )

                    } else {

                        orderRepository.addOrder(
                            order
                        )

                        Log.d(
                            tag,
                            "Added order: ${order.id}"
                        )
                    }

                } catch (e: Exception) {

                    Log.e(
                        tag,
                        "Failed to save order ${order.id}",
                        e
                    )
                }
            }

            // ====================================================
            // ORDER ITEMS
            // ====================================================

            data.orderItems.forEach { item ->

                /*
                 * Your current OrderRepository does not appear
                 * to expose add/update methods for OrderItem.
                 *
                 * Leave these for now.
                 */

                Log.d(
                    tag,
                    "OrderItem received: ${item.id}"
                )
            }

            Log.d(
                tag,
                "✅ LOCAL SAVE COMPLETE"
            )

        } catch (e: Exception) {

            Log.e(
                tag,
                "❌ LOCAL SAVE FAILED",
                e
            )

            throw e
        }
    }

    // ============================================================
    // FULL SYNC
    // ============================================================

    suspend fun syncAll(): Result<Unit> {

        val userId = getUserId()

        return try {

            Log.d(
                tag,
                "========================================"
            )

            Log.d(
                tag,
                "FULL SYNC for user: $userId"
            )

            /*
             * IMPORTANT:
             *
             * We download first because the current system uses
             * Supabase as the shared state.
             *
             * However, do NOT use this blindly when two devices
             * have independent unsynced changes.
             *
             * Automatic local changes should use uploadData()
             * through SyncTrigger.
             */

            val downloadResult =
                downloadData()

            if (downloadResult.isFailure) {

                Log.e(
                    tag,
                    "❌ FULL SYNC DOWNLOAD FAILED"
                )

                return Result.failure(
                    downloadResult.exceptionOrNull()
                        ?: Exception("Download failed")
                )
            }

            val uploadResult =
                uploadData()

            if (uploadResult.isFailure) {

                Log.e(
                    tag,
                    "❌ FULL SYNC UPLOAD FAILED"
                )

                return Result.failure(
                    uploadResult.exceptionOrNull()
                        ?: Exception("Upload failed")
                )
            }

            Log.d(
                tag,
                "✅ FULL SYNC COMPLETE"
            )

            Log.d(
                tag,
                "========================================"
            )

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(
                tag,
                "❌ FULL SYNC FAILED",
                e
            )

            Result.failure(e)
        }
    }
}
