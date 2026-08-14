package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.R
import com.hninakari.saletracker.core.ui.theme.Primary
import com.hninakari.saletracker.core.ui.theme.TextPrimary
import com.hninakari.saletracker.data.model.Order
import com.hninakari.saletracker.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderListScreen(
    viewModel: OrderViewModel,
    onOrderClick: (Int) -> Unit,
    onNewOrder: () -> Unit,
    onCancelOrder: (Int) -> Unit,
    onCompleteOrder: (Int) -> Unit
) {
    val orders by viewModel.draftOrders.collectAsState(initial = emptyList())
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    
    val ordersBySupplier = orders.groupBy { it.supplierPersonId ?: 0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.order_list),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Text(
                    text = "${stringResource(R.string.orders)} ${orders.size}",
                    fontSize = 13.sp,
                    color = TextPrimary.copy(alpha = 0.6f)
                )
            }
            
            Row {
                IconButton(onClick = { /* Navigate to order history */ }) {
                    Icon(Icons.Default.History, contentDescription = stringResource(R.string.history))
                }
                FloatingActionButton(
                    onClick = onNewOrder,
                    containerColor = Primary,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_order))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (orders.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("📋", fontSize = 48.sp)
                    Text(
                        text = stringResource(R.string.no_orders),
                        fontSize = 18.sp,
                        color = TextPrimary.copy(alpha = 0.6f)
                    )
                    Text(
                        text = stringResource(R.string.tap_to_create_order),
                        fontSize = 14.sp,
                        color = TextPrimary.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ordersBySupplier.forEach { (supplierId, supplierOrders) ->
                    item {
                        val supplierName = if (supplierId == 0) stringResource(R.string.no_supplier) else "Supplier #$supplierId"
                        Text(
                            text = "📦 $supplierName",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Primary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    
                    items(supplierOrders) { order ->
                        OrderCard(
                            order = order,
                            dateFormat = dateFormat,
                            onClick = { onOrderClick(order.id) },
                            onCancel = { onCancelOrder(order.id) },
                            onComplete = { onCompleteOrder(order.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    dateFormat: SimpleDateFormat,
    onClick: () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "#${order.id} - ${dateFormat.format(Date(order.date))}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    if (order.note.isNotEmpty()) {
                        Text(
                            text = order.note,
                            fontSize = 12.sp,
                            color = TextPrimary.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = "${stringResource(R.string.total)}: $${String.format("%.2f", order.totalAmount)}",
                        fontSize = 13.sp,
                        color = Primary,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Primary.copy(alpha = 0.15f),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.draft),
                        fontSize = 11.sp,
                        color = Primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(36.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(stringResource(R.string.cancel), fontSize = 12.sp)
                }
                
                Button(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f).height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(stringResource(R.string.mark_bought), fontSize = 12.sp)
                }
            }
        }
    }
}
