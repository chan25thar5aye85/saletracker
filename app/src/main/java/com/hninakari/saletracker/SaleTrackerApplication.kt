package com.hninakari.saletracker

import android.app.Application
import com.hninakari.saletracker.core.RealtimeManager
import com.hninakari.saletracker.core.SupabaseClient
import com.hninakari.saletracker.core.SyncManager
import com.hninakari.saletracker.core.SyncTrigger
import com.hninakari.saletracker.data.repository.*
import com.hninakari.saletracker.utils.LanguageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SaleTrackerApplication : Application() {

    lateinit var saleRepository: SaleRepository
    lateinit var expenseRepository: ExpenseRepository
    lateinit var transferRepository: TransferRepository
    lateinit var personRepository: PersonRepository
    lateinit var debtRepository: DebtRepository
    lateinit var productRepository: ProductRepository
    lateinit var toBuyRepository: ToBuyRepository
    lateinit var productSupplierRepository: ProductSupplierRepository
    lateinit var orderRepository: OrderRepository

    lateinit var syncManager: SyncManager
    lateinit var realtimeManager: RealtimeManager

    override fun onCreate() {
        super.onCreate()

        LanguageManager.setLocale(
            this,
            LanguageManager.getLanguage(this)
        )

        // Repositories
        saleRepository = SaleRepository(this)
        expenseRepository = ExpenseRepository(this)
        transferRepository = TransferRepository(this)
        personRepository = PersonRepository(this)
        debtRepository = DebtRepository(this)
        productRepository = ProductRepository(this)
        toBuyRepository = ToBuyRepository(this)
        productSupplierRepository = ProductSupplierRepository(this)
        orderRepository = OrderRepository(this)

        // Sync manager
        syncManager = SyncManager(
            context = this,
            saleRepository = saleRepository,
            expenseRepository = expenseRepository,
            transferRepository = transferRepository,
            personRepository = personRepository,
            debtRepository = debtRepository,
            productRepository = productRepository,
            toBuyRepository = toBuyRepository,
            orderRepository = orderRepository
        )

        // Automatic upload whenever local data changes
        SyncTrigger.initialize {
            val result = syncManager.uploadData()
            if (result.isFailure) {
                throw result.exceptionOrNull() ?: Exception("Upload failed")
            }
        }

        // Realtime listener
        realtimeManager = RealtimeManager(
            context = this,
            supabaseClient = SupabaseClient.instance,
            syncManager = syncManager
        )

        realtimeManager.startListening()

        // Download latest cloud data on startup
        CoroutineScope(Dispatchers.IO).launch {
            syncManager.downloadData()
        }
    }

    fun startRealtimeListening() {
        realtimeManager.startListening()
    }

    fun stopRealtimeListening() {
        realtimeManager.stopListening()
    }
}
