package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.R
import com.hninakari.saletracker.core.ui.theme.Primary
import com.hninakari.saletracker.core.ui.theme.TextPrimary
import com.hninakari.saletracker.data.model.Transfer
import com.hninakari.saletracker.data.model.TransferDirection
import com.hninakari.saletracker.data.model.TransferService
import com.hninakari.saletracker.utils.NumberUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferEntryScreen(
    onTransferAdded: (Transfer) -> Unit = {}
) {
    var amount by remember { mutableStateOf("") }
    var fee by remember { mutableStateOf("") }
    var selectedService by remember { mutableStateOf(TransferService.KPAY) }
    var selectedDirection by remember { mutableStateOf(TransferDirection.OUT) }
    var notes by remember { mutableStateOf("") }
    
    var amountError by remember { mutableStateOf(false) }
    var feeError by remember { mutableStateOf(false) }
    
    // Dropdown states
    var directionExpanded by remember { mutableStateOf(false) }
    var serviceExpanded by remember { mutableStateOf(false) }
    
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Direction and Service in a compact row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Direction Dropdown
            ExposedDropdownMenuBox(
                expanded = directionExpanded,
                onExpandedChange = { directionExpanded = !directionExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = if (selectedDirection == TransferDirection.IN) "📥 IN" else "📤 OUT",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("လမ်းကြောင်း") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = directionExpanded) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                )
                
                ExposedDropdownMenu(
                    expanded = directionExpanded,
                    onDismissRequest = { directionExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("📥 IN") },
                        onClick = {
                            selectedDirection = TransferDirection.IN
                            directionExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("📤 OUT") },
                        onClick = {
                            selectedDirection = TransferDirection.OUT
                            directionExpanded = false
                        }
                    )
                }
            }
            
            // Service Dropdown
            ExposedDropdownMenuBox(
                expanded = serviceExpanded,
                onExpandedChange = { serviceExpanded = !serviceExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = if (selectedService == TransferService.KPAY) "KPay" else "WavePay",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("ဝန်ဆောင်မှု") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceExpanded) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                )
                
                ExposedDropdownMenu(
                    expanded = serviceExpanded,
                    onDismissRequest = { serviceExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("KPay") },
                        onClick = {
                            selectedService = TransferService.KPAY
                            serviceExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("WavePay") },
                        onClick = {
                            selectedService = TransferService.WAVEPAY
                            serviceExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Amount field - using label instead of placeholder
        OutlinedTextField(
            value = amount,
            onValueChange = { 
                val englishDigits = NumberUtils.toEnglishDigits(it)
                val filtered = englishDigits.filter { char -> char.isDigit() || char == '.' }
                amount = filtered
                amountError = false
            },
            label = { Text(stringResource(R.string.amount)) },
            modifier = Modifier
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = amountError,
            supportingText = {
                if (amountError) {
                    Text("Invalid", fontSize = 10.sp)
                }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Fee field - using label instead of placeholder
        OutlinedTextField(
            value = fee,
            onValueChange = { 
                val englishDigits = NumberUtils.toEnglishDigits(it)
                val filtered = englishDigits.filter { char -> char.isDigit() || char == '.' }
                fee = filtered
                feeError = false
            },
            label = { Text(stringResource(R.string.fee)) },
            modifier = Modifier
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = feeError,
            supportingText = {
                if (feeError) {
                    Text("Invalid", fontSize = 10.sp)
                }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text(stringResource(R.string.notes_optional)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 3
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Button(
            onClick = {
                val cleanAmount = NumberUtils.toDouble(amount)
                val cleanFee = NumberUtils.toDouble(fee) ?: 0.0
                
                var valid = true
                if (cleanAmount == null || cleanAmount <= 0.0) {
                    amountError = true
                    valid = false
                }
                if (cleanFee < 0.0) {
                    feeError = true
                    valid = false
                }
                
                if (valid) {
                    val transfer = Transfer(
                        service = selectedService,
                        direction = selectedDirection,
                        amount = cleanAmount!!,
                        fee = cleanFee,
                        customerName = "",
                        customerPhone = "",
                        notes = notes.trim()
                    )
                    onTransferAdded(transfer)
                    amount = ""
                    fee = ""
                    notes = ""
                    selectedService = TransferService.KPAY
                    selectedDirection = TransferDirection.OUT
                    amountError = false
                    feeError = false
                    focusManager.clearFocus()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text(stringResource(R.string.add_transfer_button), fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        TextButton(
            onClick = {
                amount = ""
                fee = ""
                notes = ""
                selectedService = TransferService.KPAY
                selectedDirection = TransferDirection.OUT
                amountError = false
                feeError = false
                focusManager.clearFocus()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.clear), fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}
