package com.hninakari.saletracker.data.repository

import android.content.Context
import com.hninakari.saletracker.core.SyncTrigger
import com.hninakari.saletracker.data.database.PurchaseDatabase
import com.hninakari.saletracker.data.database.ProductSupplierEntity
import com.hninakari.saletracker.data.model.ProductSupplier
import com.hninakari.saletracker.data.model.ProductWithSuppliers
import com.hninakari.saletracker.data.model.SupplierPrice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ProductSupplierRepository(context: Context) {

    private val database = PurchaseDatabase.getDatabase(context)
    private val dao = database.productSupplierDao()
    private val productDao = database.productDao()
    private val personRepository = PersonRepository(context)

    private fun toEntity(model: ProductSupplier): ProductSupplierEntity {
        return ProductSupplierEntity(
            id = model.id,
            productId = model.productId,
            supplierPersonId = model.supplierPersonId,
            price = model.price,
            isDefault = model.isDefault,
            isDeleted = model.isDeleted
        )
    }

    private fun toModel(entity: ProductSupplierEntity): ProductSupplier {
        return ProductSupplier(
            id = entity.id,
            productId = entity.productId,
            supplierPersonId = entity.supplierPersonId,
            price = entity.price,
            isDefault = entity.isDefault,
            isDeleted = entity.isDeleted
        )
    }

    suspend fun getSupplierPrice(
        productId: Int,
        supplierId: Int
    ): ProductSupplier? {

        val entity = dao.getSupplierPrice(
            productId,
            supplierId
        )

        return entity?.let {
            toModel(it)
        }
    }

    suspend fun getDefaultSupplier(
        productId: Int
    ): ProductSupplier? {

        val entity =
            dao.getDefaultSupplierForProduct(productId)

        return entity?.let {
            toModel(it)
        }
    }

    fun getSuppliersForProduct(
        productId: Int
    ): Flow<List<ProductSupplier>> {

        return dao
            .getSuppliersForProduct(productId)
            .map { entities ->
                entities.map {
                    toModel(it)
                }
            }
    }

    fun getProductsForSupplier(
        supplierId: Int
    ): Flow<List<ProductSupplier>> {

        return dao
            .getProductsForSupplier(supplierId)
            .map { entities ->
                entities.map {
                    toModel(it)
                }
            }
    }

    suspend fun addSupplierPrice(
        supplier: ProductSupplier
    ) {

        if (supplier.isDefault) {
            dao.clearDefaultSupplier(
                supplier.productId
            )
        }

        dao.insertProductSupplier(
            toEntity(supplier)
        )

        // Local database changed.
        // Upload the latest data to Supabase.
        SyncTrigger.triggerUpload()
    }

    suspend fun updateSupplierPrice(
        supplier: ProductSupplier
    ) {

        if (supplier.isDefault) {
            dao.clearDefaultSupplier(
                supplier.productId
            )
        }

        dao.updateProductSupplier(
            toEntity(supplier)
        )

        // Local database changed.
        SyncTrigger.triggerUpload()
    }

    suspend fun deleteSupplierPrice(
        id: Int
    ) {

        dao.softDeleteProductSupplier(id)

        // Local database changed.
        SyncTrigger.triggerUpload()
    }

    suspend fun setDefaultSupplier(
        productId: Int,
        supplierId: Int
    ) {

        dao.clearDefaultSupplier(productId)

        val entity =
            dao.getSupplierPrice(
                productId,
                supplierId
            )

        entity?.let {

            val updated = it.copy(
                isDefault = true
            )

            dao.updateProductSupplier(
                updated
            )
        }

        // Local database changed.
        SyncTrigger.triggerUpload()
    }

    fun getProductWithSuppliers(
        productId: Int
    ): Flow<ProductWithSuppliers> {

        return productDao
            .getAllProducts()
            .map { products ->
                products.find {
                    it.id == productId
                }
            }
            .combine(
                dao.getSuppliersForProduct(productId)
            ) { product, suppliers ->

                val productModel =
                    product?.let {

                        com.hninakari.saletracker.data.model.Product(
                            id = it.id,
                            name = it.name,
                            price = it.price,
                            isDeleted = it.isDeleted
                        )

                    } ?: com.hninakari.saletracker.data.model.Product(
                        id = 0,
                        name = "",
                        price = 0.0,
                        isDeleted = false
                    )

                ProductWithSuppliers(
                    product = productModel,

                    suppliers = suppliers.map { supplier ->

                        SupplierPrice(
                            supplier =
                                com.hninakari.saletracker.data.model.Person(
                                    id = supplier.supplierPersonId,
                                    name = "Supplier ${supplier.supplierPersonId}",
                                    phone = "",
                                    type = com.hninakari.saletracker.data.model.PersonType.SUPPLIER
                                ),
                            price = supplier.price,
                            isDefault = supplier.isDefault
                        )
                    }
                )
            }
    }
}
