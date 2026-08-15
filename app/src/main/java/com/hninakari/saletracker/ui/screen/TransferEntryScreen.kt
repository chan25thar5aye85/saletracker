package com.hninakari.saletracker.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hninakari.saletracker.R
import com.hninakari.saletracker.data.model.Transfer
import com.hninakari.saletracker.data.model.TransferDirection
import com.hninakari.saletracker.data.model.TransferService
import com.hninakari.saletracker.utils.DateUtils
import com.hninakari.saletracker.utils.NumberUtils
import com.hninakari.saletracker.viewmodel.TransferViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferEntryScreen(
    transferViewModel: TransferViewModel? = null,
    onTransferAdded: (Transfer) -> Unit = {},
    onHistoryClick: () -> Unit = {}
) {
    val viewModel = transferViewModel ?: return
    val colorScheme = MaterialTheme.colorScheme
    
    val allTransfers by viewModel.filteredTransfers.collectAsState(initial = emptyList())
    
    val todayTransfers = remember(allTransfers) {
        val todayStart = DateUtils.getFilterStartTime(DateUtils.DateFilter.TODAY)
        allTransfers.filter { 
            !it.isDeleted && it.date >= todayStart
        }
    }
    
    var amount by remember { mutableStateOf("") }
    var fee by remember { mutableStateOf("") }
    var selectedService by remember { mutableStateOf(TransferService.KPAY) }
    var selectedDirection by remember { mutableStateOf(TransferDirection.OUT) }
    var notes by remember { mutableStateOf("") }

    var amountError by remember { mutableStateOf(false) }
    var feeError by remember { mutableStateOf(false) }

    var directionExpanded by remember { mutableStateOf(false) }
    var serviceExpanded by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var fabOffsetX by remember { mutableStateOf(0f) }
    var fabOffsetY by remember { mutableStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "🔄 Add Transfer",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // ============================================================
            // FORM CARD
            // ============================================================
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Direction and Service
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = directionExpanded,
                            onExpandedChange = { directionExpanded = !directionExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = if (selectedDirection == TransferDirection.IN) "📥 IN" else "📤 OUT",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("လမ်းကြောင်း", fontSize = 12.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = directionExpanded) },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = directionExpanded,
                                onDismissRequest = { directionExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("📥 IN", fontSize = 14.sp) },
                                    onClick = { 
                                        selectedDirection = TransferDirection.IN
                                        directionExpanded = false 
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("📤 OUT", fontSize = 14.sp) },
                                    onClick = { 
                                        selectedDirection = TransferDirection.OUT
                                        directionExpanded = false 
                                    }
                                )
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = serviceExpanded,
                            onExpandedChange = { serviceExpanded = !serviceExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = if (selectedService == TransferService.KPAY) "KPay" else "WavePay",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("ဝန်ဆောင်မှု", fontSize = 12.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceExpanded) },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = serviceExpanded,
                                onDismissRequest = { serviceExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("KPay", fontSize = 14.sp) },
                                    onClick = { 
                                        selectedService = TransferService.KPAY
                                        serviceExpanded = false 
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("WavePay", fontSize = 14.sp) },
                                    onClick = { 
                                        selectedService = TransferService.WAVEPAY
                                        serviceExpanded = false 
                                    }
                                )
                            }
                        }
                    }

                    // Amount and Fee in one row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = amount,
                            onValueChange = {
                                val englishDigits = NumberUtils.toEnglishDigits(it)
                                val filtered = englishDigits.filter { char -> char.isDigit() || char == '.' }
                                amount = filtered
                                amountError = false
                            },
                            label = { Text(stringResource(R.string.amount), fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            isError = amountError,
                            supportingText = {
                                if (amountError) {
                                    Text("Invalid", fontSize = 11.sp, color = colorScheme.error)
                                }
                            },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        )

                        OutlinedTextField(
                            value = fee,
                            onValueChange = {
                                val englishDigits = NumberUtils.toEnglishDigits(it)
                                val filtered = englishDigits.filter { char -> char.isDigit() || char == '.' }
                                fee = filtered
                                feeError = false
                            },
                            label = { Text(stringResource(R.string.fee), fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            isError = feeError,
                            supportingText = {
                                if (feeError) {
                                    Text("Invalid", fontSize = 11.sp, color = colorScheme.error)
                                }
                            },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        )
                    }

                    // Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text(stringResource(R.string.notes_optional), fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )

                    // Add Transfer Button
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            stringResource(R.string.add_transfer_button), 
                            fontSize = 16.sp
                        )
                    }

                    // Clear Button
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
                }
            }

            // Today's Transfers History
            Spacer(modifier = Modifier.height(24.dp))
            
            Divider(
                color = colorScheme.onSurface.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📋 Today's Transfers",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface
                )
                
                Text(
                    text = "${todayTransfers.size} entries",
                    fontSize = 12.sp,
                    color = colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (todayTransfers.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transfers today",
                            fontSize = 14.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                val displayTransfers = todayTransfers
                    .sortedByDescending { it.date }
                    .take(4)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("#", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary, modifier = Modifier.width(30.dp))
                    Text("Amount", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary, modifier = Modifier.weight(1f))
                    Text("Dir", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary, modifier = Modifier.weight(0.5f))
                    Text("Service", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary, modifier = Modifier.weight(0.7f))
                }
                
                displayTransfers.forEachIndexed { index, transfer ->
                    val rowColor = if (index % 2 == 0) {
                        colorScheme.surface
                    } else {
                        colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(rowColor)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${displayTransfers.size - index}", fontSize = 13.sp, color = colorScheme.onSurface, modifier = Modifier.width(30.dp))
                        Text("${transfer.amount}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colorScheme.onSurface, modifier = Modifier.weight(1f))
                        Text(if (transfer.direction == TransferDirection.IN) "📥" else "📤", fontSize = 15.sp, modifier = Modifier.weight(0.5f))
                        Text(if (transfer.service == TransferService.KPAY) "KPay" else "Wave", fontSize = 12.sp, color = colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.7f))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }

        // ============================================================
        // FLOATING HISTORY BUTTON (moved up 200dp)
        // ============================================================
        
        FloatingActionButton(
            onClick = onHistoryClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset { 
                    IntOffset(
                        x = fabOffsetX.roundToInt(),
                        y = fabOffsetY.roundToInt() - 200.dp.value.roundToInt()
                    )
                }
                .padding(16.dp)
                .size(52.dp)
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
            containerColor = colorScheme.primary.copy(alpha = 0.85f),
            contentColor = colorScheme.onPrimary,
            shape = RoundedCornerShape(16.dp),
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "View All Transfer History",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
