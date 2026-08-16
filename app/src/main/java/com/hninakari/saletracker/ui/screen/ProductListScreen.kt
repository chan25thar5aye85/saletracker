package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.hninakari.saletracker.data.model.Product
import com.hninakari.saletracker.viewmodel.ProductViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ProductListScreen(
    viewModel: ProductViewModel,
    onAddProduct: () -> Unit,
    onEditProduct: (Product) -> Unit
) {
    val allProducts by viewModel.allProducts.collectAsState(initial = emptyList())
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var showArchived by remember { mutableStateOf(false) }
    
    // Filter products
    val filteredProducts = remember(allProducts, searchQuery, showArchived) {
        allProducts.filter { product ->
            val matchesSearch = searchQuery.isEmpty() || 
                product.name.lowercase().contains(searchQuery.lowercase())
            val matchesArchive = if (showArchived) {
                product.isDeleted
            } else {
                !product.isDeleted
            }
            matchesSearch && matchesArchive
        }
    }
    
    // FAB position state
    var fabOffsetX by remember { mutableStateOf(0f) }
    var fabOffsetY by remember { mutableStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp)
        ) {
            // Title
            Text(
                text = "📦 Products",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search products...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f)
                )
            )
            
            // Show archived toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Show archived: ${if (showArchived) "✅" else "❌"}",
                    fontSize = 14.sp,
                    color = colorScheme.onSurfaceVariant
                )
                Switch(
                    checked = showArchived,
                    onCheckedChange = { showArchived = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorScheme.primary,
                        checkedTrackColor = colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredProducts.isEmpty()) {
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
                        Text("📦", fontSize = 48.sp)
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No products found" else stringResource(R.string.no_products),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = stringResource(R.string.tap_to_add_product),
                                fontSize = 14.sp,
                                color = colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredProducts) { product ->
                        ProductItem(
                            product = product,
                            onEdit = { onEditProduct(product) },
                            showArchived = showArchived
                        )
                    }
                }
            }
        }

        // ============================================================
        // FLOATING ADD BUTTON (moved up 200dp)
        // ============================================================
        
        FloatingActionButton(
            onClick = onAddProduct,
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
                contentDescription = "Add Product",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    onEdit: () -> Unit,
    showArchived: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    val isArchived = product.isDeleted
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .shadow(
                elevation = if (isArchived) 1.dp else 2.dp,
                shape = RoundedCornerShape(10.dp),
                clip = false
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isArchived) 
                colorScheme.surfaceVariant.copy(alpha = 0.5f) 
            else 
                colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = product.name,
                        fontSize = 16.sp,
                        fontWeight = if (isArchived) FontWeight.Normal else FontWeight.Medium,
                        color = if (isArchived) 
                            colorScheme.onSurface.copy(alpha = 0.5f)
                        else 
                            colorScheme.onSurface
                    )
                    
                    if (isArchived) {
                        Text(
                            text = "📦 Archived",
                            fontSize = 10.sp,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .background(
                                    color = colorScheme.onSurface.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                
                Text(
                    text = "$${String.format("%.2f", product.price)}",
                    fontSize = 14.sp,
                    color = if (isArchived) 
                        colorScheme.onSurface.copy(alpha = 0.4f)
                    else 
                        colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit),
                    tint = if (isArchived)
                        colorScheme.onSurface.copy(alpha = 0.3f)
                    else
                        colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}
