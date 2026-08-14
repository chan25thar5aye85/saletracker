package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hninakari.saletracker.R
import com.hninakari.saletracker.core.ui.theme.Primary
import com.hninakari.saletracker.core.ui.theme.TextPrimary
import com.hninakari.saletracker.data.model.Product
import com.hninakari.saletracker.data.model.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToBuyItemDialog(
    products: List<Product>,
    onDismiss: () -> Unit,
    onAdd: (Int, Int, String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedProductId by remember { mutableStateOf(products.firstOrNull()?.id ?: 0) }
    var quantity by remember { mutableStateOf("1") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var note by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    val filteredProducts = if (searchQuery.isEmpty()) {
        products
    } else {
        products.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 480.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.add_item_to_buy),
                    fontSize = 18.sp,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            isDropdownExpanded = true
                            if (filteredProducts.size == 1) {
                                selectedProductId = filteredProducts.first().id
                            }
                        },
                        label = { Text(stringResource(R.string.search_product), fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { 
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) 
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = TextPrimary.copy(alpha = 0.3f)
                        ),
                        singleLine = true
                    )
                    
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded && filteredProducts.isNotEmpty(),
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        filteredProducts.forEach { product ->
                            DropdownMenuItem(
                                text = { 
                                    Column {
                                        Text(
                                            product.name,
                                            fontSize = 14.sp,
                                            fontWeight = if (product.id == selectedProductId) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            "$${String.format("%.2f", product.price)}",
                                            fontSize = 11.sp,
                                            color = TextPrimary.copy(alpha = 0.5f)
                                        )
                                    }
                                },
                                onClick = {
                                    selectedProductId = product.id
                                    searchQuery = product.name
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                
                if (searchQuery.isNotEmpty() && selectedProductId != 0) {
                    val selected = products.find { it.id == selectedProductId }
                    if (selected != null) {
                        Text(
                            text = "${stringResource(R.string.selected)}: ${selected.name} - $${String.format("%.2f", selected.price)}",
                            fontSize = 12.sp,
                            color = Primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { 
                        val filtered = it.filter { char -> char.isDigit() }
                        quantity = filtered
                    },
                    label = { Text(stringResource(R.string.quantity), fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextPrimary.copy(alpha = 0.3f)
                    )
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = stringResource(R.string.priority),
                    fontSize = 11.sp,
                    color = TextPrimary.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Priority.values().forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { 
                                Text(
                                    when (p) {
                                        Priority.HIGH -> stringResource(R.string.high)
                                        Priority.MEDIUM -> stringResource(R.string.medium)
                                        Priority.LOW -> stringResource(R.string.low)
                                    },
                                    fontSize = 10.sp
                                )
                            },
                            modifier = Modifier.weight(1f).height(28.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary.copy(alpha = 0.2f),
                                selectedLabelColor = Primary
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(stringResource(R.string.notes_optional), fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 2,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextPrimary.copy(alpha = 0.3f)
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TextButton(
                        onClick = { 
                            focusManager.clearFocus()
                            onDismiss() 
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.cancel), fontSize = 13.sp)
                    }
                    
                    Button(
                        onClick = {
                            val qty = quantity.toIntOrNull()
                            if (selectedProductId != 0 && qty != null && qty > 0) {
                                focusManager.clearFocus()
                                onAdd(selectedProductId, qty, priority.name, note)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(R.string.add_item), fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
