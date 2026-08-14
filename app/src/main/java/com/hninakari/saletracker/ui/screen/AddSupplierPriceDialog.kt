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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hninakari.saletracker.core.ui.theme.Primary
import com.hninakari.saletracker.core.ui.theme.TextPrimary
import com.hninakari.saletracker.data.model.Person

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSupplierPriceDialog(
    productName: String,
    suppliers: List<Person>,
    existingSupplierId: Int? = null,
    existingPrice: Double? = null,
    onDismiss: () -> Unit,
    onSave: (Int, Double, Boolean) -> Unit
) {
    var selectedSupplierId by remember { mutableStateOf(existingSupplierId ?: suppliers.firstOrNull()?.id ?: 0) }
    var price by remember { mutableStateOf(existingPrice?.toString() ?: "") }
    var isDefault by remember { mutableStateOf(false) }
    var supplierExpanded by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val isEditing = existingSupplierId != null

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 400.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isEditing) "စျေးနှုန်းပြင်ရန်" else "ပေးသွင်းသူစျေးထည့်ရန်",
                    fontSize = 18.sp,
                    color = Primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                
                Text(
                    text = "ပစ္စည်း: $productName",
                    fontSize = 14.sp,
                    color = TextPrimary.copy(alpha = 0.6f)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                ExposedDropdownMenuBox(
                    expanded = supplierExpanded,
                    onExpandedChange = { supplierExpanded = !supplierExpanded }
                ) {
                    OutlinedTextField(
                        value = suppliers.find { it.id == selectedSupplierId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("ပေးသွင်းသူ", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierExpanded) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
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
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = price,
                    onValueChange = { 
                        val filtered = it.filter { char -> char.isDigit() || char == '.' }
                        price = filtered
                        priceError = false
                    },
                    label = { Text("စျေးနှုန်း", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = priceError,
                    supportingText = {
                        if (priceError) {
                            Text("စျေးနှုန်းထည့်ပါ", fontSize = 10.sp)
                        }
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = TextPrimary.copy(alpha = 0.3f)
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it },
                        colors = CheckboxDefaults.colors(checkedColor = Primary)
                    )
                    Text(
                        text = "ဤပေးသွင်းသူကို မူလအဖြစ်သတ်မှတ်မည်",
                        fontSize = 13.sp,
                        color = TextPrimary.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = { 
                            focusManager.clearFocus()
                            onDismiss() 
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("မလုပ်တော့", fontSize = 14.sp)
                    }
                    
                    Button(
                        onClick = {
                            val priceValue = price.toDoubleOrNull()
                            if (priceValue == null || priceValue <= 0.0) {
                                priceError = true
                            } else {
                                focusManager.clearFocus()
                                onSave(selectedSupplierId, priceValue, isDefault)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isEditing) "သိမ်းမည်" else "ထည့်မည်", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
