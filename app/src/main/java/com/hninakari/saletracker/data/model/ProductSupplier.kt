package com.hninakari.saletracker.data.model

data class ProductSupplier(
    val id: Int = 0,
    val productId: Int,
    val supplierPersonId: Int,
    val price: Double,
    val isDefault: Boolean = false,
    val isDeleted: Boolean = false
)

data class ProductWithSuppliers(
    val product: Product,
    val suppliers: List<SupplierPrice>
)

data class SupplierPrice(
    val supplier: Person,
    val price: Double,
    val isDefault: Boolean
)
