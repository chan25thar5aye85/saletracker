package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hninakari.saletracker.R
import com.hninakari.saletracker.core.ui.theme.Primary
import com.hninakari.saletracker.core.ui.theme.TextPrimary
import com.hninakari.saletracker.data.model.Person
import com.hninakari.saletracker.viewmodel.ToBuyItemWithProduct

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderDialog(
    items: List<ToBuyItemWithProduct>,
    suppliers: List<Person>,
    onDismiss: () -> Unit,
    onCreate: (Int?, String, List<Int>) -> Unit
) {
    var selectedItems by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var selectedSupplierId by remember { mutableStateOf<Int?>(null) }
    var note by remember { mutableStateOf("") }
    var supplierExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    val totalAmount = items
        .filter { it.item.id in selectedItems }
        .sumOf { (it.product?.price ?: 0.0) * it.item.quantity }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 550.dp),
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
                    text = stringResource(R.string.new_order),
                    fontSize = 18.sp,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
                
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
                        colors = CheckboxDefaults.colors(checkedColor = Primary)
                    )
                    Text(
                        text = stringResource(R.string.select_all),
                        fontSize = 13.sp,
                        color = TextPrimary.copy(alpha = 0.6f)
                    )
                    Text(
                        text = " (${selectedItems.size}/${items.size})",
                        fontSize = 12.sp,
                        color = Primary
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 180.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(items) { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedItems.contains(item.item.id),
                                    onCheckedChange = { checked ->
                                        selectedItems = if (checked) {
                                            selectedItems + item.item.id
                                        } else {
                                            selectedItems - item.item.id
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = Primary)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.product?.name ?: "Unknown",
                                        fontSize = 13.sp,
                                        fontWeight = if (selectedItems.contains(item.item.id)) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = "×${item.item.quantity} - $${String.format("%.2f", item.product?.price ?: 0.0)}",
                                        fontSize = 11.sp,
                                        color = TextPrimary.copy(alpha = 0.5f)
                                    )
                                }
                                Text(
                                    text = "$${String.format("%.2f", (item.product?.price ?: 0.0) * item.item.quantity)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${stringResource(R.string.total)}:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "$${String.format("%.2f", totalAmount)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                ExposedDropdownMenuBox(
                    expanded = supplierExpanded,
                    onExpandedChange = { supplierExpanded = !supplierExpanded }
                ) {
                    OutlinedTextField(
                        value = suppliers.find { it.id == selectedSupplierId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.supplier), fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierExpanded) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = TextPrimary.copy(alpha = 0.3f)
                        )
                    )
                    
                    ExposedDropdownMenu(
                        expanded = supplierExpanded,
                        onDismissRequest = { supplierExpanded = false }
                    ) {
                        suppliers.forEach { supplier ->
                            DropdownMenuItem(
                                text = { Text(supplier.name) },
                                onClick = {
                                    selectedSupplierId = supplier.id
                                    supplierExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(stringResource(R.string.notes), fontSize = 12.sp) },
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
                            if (selectedItems.isNotEmpty()) {
                                focusManager.clearFocus()
                                onCreate(selectedSupplierId, note, selectedItems.toList())
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedItems.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedItems.isNotEmpty()) Primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (selectedItems.isNotEmpty()) MaterialTheme.colorScheme.onPrimary else TextPrimary.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(R.string.create_order), fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
