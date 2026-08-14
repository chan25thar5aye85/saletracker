package com.hninakari.saletracker.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [
        ProductEntity::class,
        ToBuyItemEntity::class,
        PurchaseBatchEntity::class,
        PurchaseBatchItemEntity::class,
        ProductSupplierEntity::class,
        OrderEntity::class,
        OrderItemEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class PurchaseDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun toBuyItemDao(): ToBuyItemDao
    abstract fun purchaseBatchDao(): PurchaseBatchDao
    abstract fun purchaseBatchItemDao(): PurchaseBatchItemDao
    abstract fun productSupplierDao(): ProductSupplierDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao

    companion object {
        @Volatile
        private var INSTANCE: PurchaseDatabase? = null

        fun getDatabase(context: Context): PurchaseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PurchaseDatabase::class.java,
                    "purchase_database"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
