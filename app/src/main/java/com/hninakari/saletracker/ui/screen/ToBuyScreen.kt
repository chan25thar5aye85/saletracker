package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.R
import com.hninakari.saletracker.data.model.Priority
import com.hninakari.saletracker.viewmodel.ToBuyItemWithProduct
import com.hninakari.saletracker.viewmodel.ToBuyViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ToBuyScreen(
    viewModel: ToBuyViewModel,
    onAddItem: () -> Unit,
    onMarkBought: (List<Int>) -> Unit,
    onCreateOrder: (List<Int>) -> Unit,
    onHistoryClick: () -> Unit
) {
    val items by viewModel.activeItemsWithDetails.collectAsState(initial = emptyList())
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    
    val totalItems = items.size
    val totalEstimatedCost = items.sumOf { 
        (it.product?.price ?: 0.0) * it.item.quantity 
    }
    
    var selectedItems by remember { mutableStateOf<Set<Int>>(emptySet()) }
    
    // FAB position state
    var fabOffsetX by remember { mutableStateOf(0f) }
    var fabOffsetY by remember { mutableStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 100.dp)
        ) {
            // ============================================================
            // TITLE
            // ============================================================
            
            Text(
                text = "🛒 To Buy List",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${totalItems} items • Estimated: $${String.format("%.2f", totalEstimatedCost)}",
                    fontSize = 13.sp,
                    color = colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                // History button
                IconButton(onClick = onHistoryClick) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = stringResource(R.string.history),
                        tint = colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Select All row
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
                    colors = CheckboxDefaults.colors(checkedColor = colorScheme.primary)
                )
                Text(
                    text = stringResource(R.string.select_all),
                    fontSize = 13.sp,
                    color = colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            if (items.isEmpty()) {
                // Empty state
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = stringResource(R.string.tap_to_add_item),
                            fontSize = 14.sp,
                            color = colorScheme.onSurface.copy(alpha = 0.4f)
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
            
            // Create Order button
            if (items.isNotEmpty()) {
                Button(
                    onClick = { 
                        if (selectedItems.isNotEmpty()) {
                            onCreateOrder(selectedItems.toList())
                            selectedItems = emptySet()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 8.dp),
                    enabled = selectedItems.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedItems.isNotEmpty()) colorScheme.primary else colorScheme.surfaceVariant,
                        contentColor = if (selectedItems.isNotEmpty()) colorScheme.onPrimary else colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = stringResource(R.string.create_order))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Create Order (${selectedItems.size})", 
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ============================================================
        // FLOATING ADD BUTTON (moved up 200dp)
        // ============================================================
        
        FloatingActionButton(
            onClick = onAddItem,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset { 
                    IntOffset(
                        x = fabOffsetX.roundToInt(),
                        y = fabOffsetY.roundToInt() - 200.dp.value.roundToInt()
                    )
                }
                .padding(16.dp)
                .size(56.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            fabOffsetX += dragAmount.x * 0.8f
                            fabOffsetY += dragAmount.y * 0.8f
                        },
                        onDragEnd = {
                            scope.launch {
                                delay(100)
                                val steps = 20
                                val duration = 150
                                val stepDuration = duration / steps
                                for (i in 1..steps) {
                                    val progress = 1f - (i.toFloat() / steps)
                                    val easedProgress = progress * progress
                                    fabOffsetX = fabOffsetX * easedProgress
                                    fabOffsetY = fabOffsetY * easedProgress
                                    delay(stepDuration.toLong())
                                }
                                fabOffsetX = 0f
                                fabOffsetY = 0f
                            }
                        }
                    )
                },
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp
            )
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Item",
                modifier = Modifier.size(28.dp)
            )
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
    val colorScheme = MaterialTheme.colorScheme
    val priorityColor = when (item.item.priority) {
        Priority.HIGH -> colorScheme.error
        Priority.MEDIUM -> colorScheme.tertiary
        Priority.LOW -> colorScheme.secondary
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
            containerColor = if (isSelected) colorScheme.primary.copy(alpha = 0.08f) else colorScheme.surface
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
                colors = CheckboxDefaults.colors(checkedColor = colorScheme.primary)
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
                        color = colorScheme.onSurface
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
                        color = colorScheme.onSurface.copy(alpha = 0.6f)
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
                color = colorScheme.primary
            )
        }
    }
}
