package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.background
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
import com.hninakari.saletracker.data.model.Priority
import com.hninakari.saletracker.viewmodel.ToBuyItemWithProduct
import com.hninakari.saletracker.viewmodel.ToBuyViewModel

@Composable
fun ToBuyScreen(
    viewModel: ToBuyViewModel,
    onAddItem: () -> Unit,
    onMarkBought: (List<Int>) -> Unit,
    onCreateOrder: (List<Int>) -> Unit,
    onHistoryClick: () -> Unit
) {
    val items by viewModel.activeItemsWithDetails.collectAsState(initial = emptyList())
    
    val totalItems = items.size
    val totalEstimatedCost = items.sumOf { 
        (it.product?.price ?: 0.0) * it.item.quantity 
    }
    
    var selectedItems by remember { mutableStateOf<Set<Int>>(emptySet()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Stats row (no title, title is in TopAppBar)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${stringResource(R.string.items_count)} $totalItems ${stringResource(R.string.entries)} | ${stringResource(R.string.estimated_total)} $${String.format("%.2f", totalEstimatedCost)}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Row {
                IconButton(onClick = onHistoryClick) {
                    Icon(Icons.Default.History, contentDescription = stringResource(R.string.history))
                }
                FloatingActionButton(
                    onClick = onAddItem,
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_item), modifier = Modifier.size(20.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = selectedItems.isNotEmpty() && selectedItems.size == items.size,
                onCheckedChange = { checked ->
                    selectedItems = if (checked) {
                        items.map { it.item.id }.toSet()
                    } else {
                        emptySet()
                    }
                },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
            Text(
                text = stringResource(R.string.select_all),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        if (items.isEmpty()) {
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
                    Text("🛒", fontSize = 48.sp)
                    Text(
                        text = stringResource(R.string.no_items_to_buy),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = stringResource(R.string.tap_to_add_item),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { itemWithProduct ->
                    ToBuyItemRow(
                        item = itemWithProduct,
                        isSelected = selectedItems.contains(itemWithProduct.item.id),
                        onToggle = {
                            selectedItems = if (selectedItems.contains(it)) {
                                selectedItems - it
                            } else {
                                selectedItems + it
                            }
                        }
                    )
                }
            }
        }
        
        if (items.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { 
                        if (selectedItems.isNotEmpty()) {
                            onCreateOrder(selectedItems.toList())
                            selectedItems = emptySet()
                        }
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    enabled = selectedItems.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedItems.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedItems.isNotEmpty()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = stringResource(R.string.create_order))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.create_order), fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun ToBuyItemRow(
    item: ToBuyItemWithProduct,
    isSelected: Boolean,
    onToggle: (Int) -> Unit
) {
    val product = item.product
    val priorityColor = when (item.item.priority) {
        Priority.HIGH -> MaterialTheme.colorScheme.error
        Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
        Priority.LOW -> MaterialTheme.colorScheme.secondary
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle(item.item.id) },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product?.name ?: "Unknown",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (item.item.note.isNotEmpty()) {
                        Text(text = "📝", fontSize = 12.sp)
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.item.quantity} × $${String.format("%.2f", product?.price ?: 0.0)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(priorityColor, RoundedCornerShape(50))
                    )
                    Text(
                        text = when (item.item.priority) {
                            Priority.HIGH -> stringResource(R.string.high)
                            Priority.MEDIUM -> stringResource(R.string.medium)
                            Priority.LOW -> stringResource(R.string.low)
                        },
                        fontSize = 10.sp,
                        color = priorityColor
                    )
                }
            }
            
            Text(
                text = "$${String.format("%.2f", (product?.price ?: 0.0) * item.item.quantity)}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
