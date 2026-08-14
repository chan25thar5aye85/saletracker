package com.hninakari.saletracker.data.repository

import android.content.Context
import com.hninakari.saletracker.core.SyncTrigger
import com.hninakari.saletracker.data.database.PurchaseDatabase
import com.hninakari.saletracker.data.database.OrderEntity
import com.hninakari.saletracker.data.database.OrderItemEntity
import com.hninakari.saletracker.data.model.Order
import com.hninakari.saletracker.data.model.OrderItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class OrderRepository(context: Context) {

    private val database = PurchaseDatabase.getDatabase(context)
    private val orderDao = database.orderDao()
    private val orderItemDao = database.orderItemDao()
    private val toBuyItemDao = database.toBuyItemDao()
    private val productDao = database.productDao()

    private fun toEntity(order: Order): OrderEntity {
        return OrderEntity(
            id = order.id,
            supplierPersonId = order.supplierPersonId,
            date = order.date,
            totalAmount = order.totalAmount,
            note = order.note,
            status = order.status,
            expenseId = order.expenseId,
            isDeleted = order.isDeleted
        )
    }

    private fun toModel(entity: OrderEntity): Order {
        return Order(
            id = entity.id,
            supplierPersonId = entity.supplierPersonId,
            date = entity.date,
            totalAmount = entity.totalAmount,
            note = entity.note,
            status = entity.status,
            expenseId = entity.expenseId,
            isDeleted = entity.isDeleted
        )
    }

    private fun toItemEntity(item: OrderItem): OrderItemEntity {
        return OrderItemEntity(
            id = item.id,
            orderId = item.orderId,
            productId = item.productId,
            quantity = item.quantity,
            unitPrice = item.unitPrice,
            totalPrice = item.totalPrice,
            isDeleted = item.isDeleted
        )
    }

    private fun toItemModel(entity: OrderItemEntity): OrderItem {
        return OrderItem(
            id = entity.id,
            orderId = entity.orderId,
            productId = entity.productId,
            quantity = entity.quantity,
            unitPrice = entity.unitPrice,
            totalPrice = entity.totalPrice,
            isDeleted = entity.isDeleted
        )
    }

    fun getAllOrders(): Flow<List<Order>> {
        return orderDao.getAllOrders().map { entities ->
            entities.map { toModel(it) }
        }
    }

    fun getDraftOrders(): Flow<List<Order>> {
        return orderDao.getDraftOrders().map { entities ->
            entities.map { toModel(it) }
        }
    }

    fun getPurchasedOrders(): Flow<List<Order>> {
        return orderDao.getPurchasedOrders().map { entities ->
            entities.map { toModel(it) }
        }
    }

    suspend fun getOrderById(orderId: Int): Order? {
        val entity = orderDao.getOrderById(orderId)
        return entity?.let { toModel(it) }
    }

    fun getItemsForOrder(orderId: Int): Flow<List<OrderItem>> {
        return orderItemDao.getItemsForOrder(orderId).map { entities ->
            entities.map { toItemModel(it) }
        }
    }

    fun getAllOrderItems(): Flow<List<OrderItem>> {
        return orderItemDao.getAllOrderItems().map { entities ->
            entities.map { toItemModel(it) }
        }
    }

    suspend fun addOrder(order: Order): Long {
        val result = orderDao.insertOrder(toEntity(order))
        SyncTrigger.triggerUpload()
        return result
    }

    suspend fun updateOrder(order: Order) {
        orderDao.updateOrder(toEntity(order))
        SyncTrigger.triggerUpload()
    }

    suspend fun createOrder(
        supplierPersonId: Int?,
        note: String,
        toBuyItemIds: List<Int>
    ): Order {

        val allToBuy = toBuyItemDao.getActiveToBuyItems().first()
        val toBuyItems = allToBuy.filter { it.id in toBuyItemIds }

        val productIds = toBuyItems.map { it.productId }

        val allProducts = productDao.getAllProducts().first()
        val products = allProducts.filter { it.id in productIds }

        val orderItems = toBuyItems.map { item ->

            val product = products.find {
                it.id == item.productId
            }

            OrderItem(
                orderId = 0,
                productId = item.productId,
                quantity = item.quantity,
                unitPrice = product?.price ?: 0.0,
                totalPrice = (product?.price ?: 0.0) * item.quantity
            )
        }

        val total = orderItems.sumOf {
            it.totalPrice
        }

        val order = Order(
            supplierPersonId = supplierPersonId,
            totalAmount = total,
            note = note,
            status = "draft"
        )

        val orderId = orderDao.insertOrder(
            toEntity(order)
        )

        val itemsWithOrderId = orderItems.map {
            it.copy(
                orderId = orderId.toInt()
            )
        }

        val itemEntities = itemsWithOrderId.map {
            toItemEntity(it)
        }

        orderItemDao.insertOrderItems(itemEntities)

        toBuyItems.forEach { item ->
            toBuyItemDao.assignToOrder(
                item.id,
                orderId.toInt()
            )
        }

        SyncTrigger.triggerUpload()

        return order.copy(
            id = orderId.toInt()
        )
    }

    suspend fun completeOrder(
        orderId: Int,
        expenseId: Int?
    ) {

        orderDao.markOrderAsPurchased(orderId)

        val allItems =
            toBuyItemDao
                .getToBuyItemsByOrder(orderId)
                .first()

        allItems.forEach { item ->
            toBuyItemDao.markAsBought(item.id)
        }

        val order = orderDao.getOrderById(orderId)

        order?.let {

            val updated = it.copy(
                expenseId = expenseId
            )

            orderDao.updateOrder(updated)
        }

        SyncTrigger.triggerUpload()
    }

    suspend fun cancelOrder(orderId: Int) {

        val allItems =
            toBuyItemDao
                .getToBuyItemsByOrder(orderId)
                .first()

        allItems.forEach { item ->
            toBuyItemDao.removeFromOrder(item.id)
        }

        orderDao.softDeleteOrder(orderId)

        orderItemDao.deleteItemsByOrder(orderId)

        SyncTrigger.triggerUpload()
    }

    suspend fun deleteOrder(orderId: Int) {

        val allItems =
            toBuyItemDao
                .getToBuyItemsByOrder(orderId)
                .first()

        allItems.forEach { item ->
            toBuyItemDao.removeFromOrder(item.id)
        }

        orderDao.softDeleteOrder(orderId)

        orderItemDao.deleteItemsByOrder(orderId)

        SyncTrigger.triggerUpload()
    }
}
